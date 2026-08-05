package com.example.service;

import com.example.model.Event;
import com.example.model.EventStatus;
import com.example.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MetricsService {

    private final EventRepository eventRepository;
    private final EventService eventService;

    public MetricsService(EventRepository eventRepository, EventService eventService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    /**
     * Get system metrics.
     */
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        long queuedCount = eventService.getEventCountByStatus(EventStatus.QUEUED);
        long runningCount = eventService.getEventCountByStatus(EventStatus.RUNNING);
        long completedCount = eventService.getEventCountByStatus(EventStatus.COMPLETED);

        metrics.put("queueSize", queuedCount);
        metrics.put("activeWorkers", runningCount);
        metrics.put("completedEventCount", completedCount);
        metrics.put("totalEventCount", queuedCount + runningCount + completedCount);

        // Calculate average processing time for completed events
        double avgProcessingTime = calculateAverageProcessingTime();
        metrics.put("averageProcessingTimeMs", avgProcessingTime);

        // Calculate worker utilization percentage
        double workerUtilization = calculateWorkerUtilization(runningCount);
        metrics.put("workerUtilizationPercent", workerUtilization);

        return metrics;
    }

    /**
     * Calculate the average processing time for completed events.
     */
    private double calculateAverageProcessingTime() {
        List<Event> completedEvents = eventService.getCompletedEvents();
        if (completedEvents.isEmpty()) {
            return 0.0;
        }

        double totalTime = completedEvents.stream()
                .filter(event -> event.getStartedAt() != null && event.getCompletedAt() != null)
                .mapToLong(event -> Duration.between(event.getStartedAt(), event.getCompletedAt()).toMillis())
                .sum();

        return totalTime / completedEvents.size();
    }

    /**
     * Calculate worker utilization as a percentage (running events / total events * 100).
     */
    private double calculateWorkerUtilization(long runningCount) {
        long totalCount = eventRepository.count();
        if (totalCount == 0) {
            return 0.0;
        }

        return (double) runningCount / totalCount * 100;
    }
}
