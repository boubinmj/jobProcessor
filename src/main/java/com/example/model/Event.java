package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "processing_time_ms", nullable = false)
    private Long processingTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "result", length = 1000)
    private String result;

    public Event(EventType eventType, Integer size) {
        this.id = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.size = size;
        this.status = EventStatus.QUEUED;
        this.createdAt = LocalDateTime.now();
        // Assume base processing time is 1000ms per unit of size
        this.processingTimeMs = 1000L * size;
    }

    public Event(EventType eventType, Integer size, Long baseProcessingTimeMs) {
        this.id = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.size = size;
        this.status = EventStatus.QUEUED;
        this.createdAt = LocalDateTime.now();
        this.processingTimeMs = baseProcessingTimeMs * size;
    }
}
