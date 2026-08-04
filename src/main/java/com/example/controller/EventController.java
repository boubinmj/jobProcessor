package com.example.controller;

import com.example.model.Event;
import com.example.model.EventStatus;
import com.example.model.EventType;
import com.example.service.EventService;
import com.example.service.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;
    private final MetricsService metricsService;

    public EventController(EventService eventService, MetricsService metricsService) {
        this.eventService = eventService;
        this.metricsService = metricsService;
    }

    /**
     * Submit a new event for processing.
     * POST /api/events
     * Body: { "eventType": "CREATE_ORDER", "size": 5 }
     */
    @PostMapping
    public ResponseEntity<Event> submitEvent(@RequestBody Map<String, Object> payload) {
        try {
            String eventTypeStr = (String) payload.get("eventType");
            Integer size = ((Number) payload.get("size")).intValue();

            EventType eventType = EventType.valueOf(eventTypeStr);
            Event event = eventService.submitEvent(eventType, size);

            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (IllegalArgumentException e) {
            log.error("Invalid event type provided", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error submitting event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get event by ID.
     * GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable String id) {
        Optional<Event> event = eventService.getEventStatus(id);
        return event.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get events filtered by status.
     * GET /api/events?status=queued|running|completed
     * If no status provided, returns all events.
     */
    @GetMapping
    public ResponseEntity<List<Event>> getEvents(@RequestParam(required = false) String status) {
        List<Event> events;

        if (status != null && !status.isEmpty()) {
            try {
                EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
                events = eventService.getEventsByStatus(eventStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            events = eventService.getAllEvents();
        }

        return ResponseEntity.ok(events);
    }

    /**
     * Get queued events.
     * GET /api/events/queued
     */
    @GetMapping("/status/queued")
    public ResponseEntity<List<Event>> getQueuedEvents() {
        return ResponseEntity.ok(eventService.getQueuedEvents());
    }

    /**
     * Get running events.
     * GET /api/events/status/running
     */
    @GetMapping("/status/running")
    public ResponseEntity<List<Event>> getRunningEvents() {
        return ResponseEntity.ok(eventService.getRunningEvents());
    }

    /**
     * Get completed events.
     * GET /api/events/status/completed
     */
    @GetMapping("/status/completed")
    public ResponseEntity<List<Event>> getCompletedEvents() {
        return ResponseEntity.ok(eventService.getCompletedEvents());
    }

    /**
     * Get system metrics.
     * GET /api/events/metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(metricsService.getMetrics());
    }
}
