package com.example.repository;

import com.example.model.Event;
import com.example.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findByStatus(EventStatus status);

    List<Event> findByStatusOrderByCreatedAtDesc(EventStatus status);

    long countByStatus(EventStatus status);
}
