package com.example.worker;

import com.example.model.Event;
import com.example.model.EventStatus;
import com.example.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

@Slf4j
public class WorkerThread implements Runnable {

    private final BlockingQueue<Event> eventQueue;
    private final EventRepository eventRepository;
    private final int workerId;

    public WorkerThread(BlockingQueue<Event> eventQueue, EventRepository eventRepository, int workerId) {
        this.eventQueue = eventQueue;
        this.eventRepository = eventRepository;
        this.workerId = workerId;
    }

    @Override
    public void run() {
        log.info("Worker {} started", workerId);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Poll the blocking queue for an event
                Event event = eventQueue.take();
                processEvent(event);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Worker {} interrupted", workerId);
        }
    }

    /**
     * Process a single event.
     * Simulates processing by sleeping for the event's processing time.
     */
    private void processEvent(Event event) {
        try {
            log.info("Worker {} processing event {} (type: {}, size: {})", 
                     workerId, event.getId(), event.getEventType(), event.getSize());

            // Update event status to RUNNING
            event.setStatus(EventStatus.RUNNING);
            event.setStartedAt(LocalDateTime.now());
            eventRepository.save(event);

            // Simulate processing by sleeping for the configured duration
            Thread.sleep(event.getProcessingTimeMs());

            // Mark event as COMPLETED
            event.setStatus(EventStatus.COMPLETED);
            event.setCompletedAt(LocalDateTime.now());
            event.setResult("Successfully processed: " + event.getEventType().getDisplayName() + " (size: " + event.getSize() + ")");
            eventRepository.save(event);

            log.info("Worker {} completed event {} in {} ms", 
                     workerId, event.getId(), event.getProcessingTimeMs());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Mark event as COMPLETED but with error indication
            event.setStatus(EventStatus.COMPLETED);
            event.setCompletedAt(LocalDateTime.now());
            event.setResult("Processing interrupted");
            eventRepository.save(event);
            log.warn("Worker {} was interrupted while processing event {}", workerId, event.getId());
        } catch (Exception e) {
            log.error("Worker {} encountered an error processing event {}", workerId, event.getId(), e);
            event.setStatus(EventStatus.COMPLETED);
            event.setCompletedAt(LocalDateTime.now());
            event.setResult("Error: " + e.getMessage());
            eventRepository.save(event);
        }
    }
}
