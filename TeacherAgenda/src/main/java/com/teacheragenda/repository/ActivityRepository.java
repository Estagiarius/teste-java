package com.teacheragenda.repository;

import com.teacheragenda.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Optional: Custom query to fetch activities with their related entities if needed frequently,
    // though JPA's @ManyToOne(fetch = FetchType.EAGER) and @ManyToMany with default fetch (LAZY)
    // combined with service-layer logic often handle this.
    // For example, to fetch all activities with their related event and location (tasks are often lazy):
    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.relatedEvent LEFT JOIN FETCH a.assignedLocation ORDER BY a.scheduledTime DESC")
    List<Activity> findAllWithEventAndLocationSortedByScheduledTimeDesc();

    List<Activity> findAllByOrderByNameAsc();

    // You can add more specific queries as needed, e.g., finding activities by event, by location, etc.
    // List<Activity> findByRelatedEventId(Long eventId);
    // List<Activity> findByAssignedLocationId(Long locationId);
    // List<Activity> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
}
