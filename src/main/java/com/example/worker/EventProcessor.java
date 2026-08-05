package com.example.worker;

import com.example.model.Event;
import com.example.model.EventStatus;
import com.example.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class EventProcessor {

    private final BlockingQueue<Event> eventQueue;
    private final ExecutorService executorService;
    private final EventRepository eventRepository;

    @Value("${worker.thread.count:3}")
    private int workerThreadCount;

    private volatile boolean running = false;

    public EventProcessor(BlockingQueue<Event> eventQueue, 
                         ExecutorService executorService,
                         EventRepository eventRepository) {
        this.eventQueue = eventQueue;
        this.executorService = executorService;
        this.eventRepository = eventRepository;
    }

    /**
     * Start the worker threads that will process events from the queue.
     */
    public void startWorkers() {
        if (running) {
            log.warn("Workers are already running");
            return;
        }

        running = true;
        log.info("Starting {} worker threads", workerThreadCount);

        for (int i = 0; i < workerThreadCount; i++) {
            executorService.execute(new WorkerThread(eventQueue, eventRepository, i + 1));
        }
    }

    /**
     * Submit an event to the processing queue.
     */
    public void submitEvent(Event event) {
        try {
            // Save to database FIRST before putting in queue to avoid race conditions
            eventRepository.save(event);
            eventQueue.put(event);
            log.info("Event {} submitted to queue", event.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while submitting event", e);
        }
    }

    /**
     * Stop all worker threads and shutdown the executor service gracefully.
     */
    public void stopWorkers() {
        running = false;
        log.info("Shutting down worker threads");
        executorService.shutdown();
    }

    /**
     * Check if workers are running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Get the current size of the event queue.
     */
    public int getQueueSize() {
        return eventQueue.size();
    }
}
