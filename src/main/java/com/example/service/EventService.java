package com.example.service;

import com.example.model.Event;
import com.example.model.EventStatus;
import com.example.model.EventType;
import com.example.repository.EventRepository;
import com.example.worker.EventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventProcessor eventProcessor;

    @Value("${event.base.processing.time.ms:1000}")
    private Long baseProcessingTimeMs;

    public EventService(EventRepository eventRepository, EventProcessor eventProcessor) {
        this.eventRepository = eventRepository;
        this.eventProcessor = eventProcessor;
    }

    /**
     * Submit a new event for processing.
     */
    public Event submitEvent(EventType eventType, Integer size) {
        Event event = new Event(eventType, size, baseProcessingTimeMs);
        eventProcessor.submitEvent(event);
        return event;
    }

    /**
     * Get an event by its ID.
     */
    public Optional<Event> getEventStatus(String eventId) {
        return eventRepository.findById(eventId);
    }

    /**
     * Get all events with a specific status.
     */
    public List<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Get all queued events.
     */
    public List<Event> getQueuedEvents() {
        return getEventsByStatus(EventStatus.QUEUED);
    }

    /**
     * Get all running events.
     */
    public List<Event> getRunningEvents() {
        return getEventsByStatus(EventStatus.RUNNING);
    }

    /**
     * Get all completed events.
     */
    public List<Event> getCompletedEvents() {
        return getEventsByStatus(EventStatus.COMPLETED);
    }

    /**
     * Get all events.
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Get count of events by status.
     */
    public long getEventCountByStatus(EventStatus status) {
        return eventRepository.countByStatus(status);
    }
}
