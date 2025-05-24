package com.teacheragenda.repository;

import com.teacheragenda.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Custom query to find events within a given time range
    List<Event> findAllByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    // You can add more custom queries here if needed, for example:
    // List<Event> findByTitleContainingIgnoreCase(String title);
    // List<Event> findByLocation(String location);
}
