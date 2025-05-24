package com.teacheragenda.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob // For longer text
    private String description;

    private LocalDateTime scheduledTime;
    private Integer duration; // in minutes

    @ManyToOne(fetch = FetchType.EAGER) // Eager fetch if you often need event details with activity
    @JoinColumn(name = "event_id")
    private Event relatedEvent;

    @ManyToMany(fetch = FetchType.LAZY) // Lazy fetch as tasks might be numerous or not always needed
    @JoinTable(
        name = "activity_task",
        joinColumns = @JoinColumn(name = "activity_id"),
        inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private Set<Task> relatedTasks = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER) // Eager fetch if location is usually displayed
    @JoinColumn(name = "location_id")
    private Location assignedLocation;

    // Constructors
    public Activity() {
    }

    public Activity(String name, String description, LocalDateTime scheduledTime, Integer duration) {
        this.name = name;
        this.description = description;
        this.scheduledTime = scheduledTime;
        this.duration = duration;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Event getRelatedEvent() {
        return relatedEvent;
    }

    public void setRelatedEvent(Event relatedEvent) {
        this.relatedEvent = relatedEvent;
    }

    public Set<Task> getRelatedTasks() {
        return relatedTasks;
    }

    public void setRelatedTasks(Set<Task> relatedTasks) {
        this.relatedTasks = relatedTasks;
    }

    public Location getAssignedLocation() {
        return assignedLocation;
    }

    public void setAssignedLocation(Location assignedLocation) {
        this.assignedLocation = assignedLocation;
    }

    // Helper methods for managing tasks
    public void addTask(Task task) {
        this.relatedTasks.add(task);
        task.getActivitiesInternal().add(this); // Assuming Task has a bidirectional mapping
    }

    public void removeTask(Task task) {
        this.relatedTasks.remove(task);
        task.getActivitiesInternal().remove(this); // Assuming Task has a bidirectional mapping
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", duration=" + duration +
                ", eventId=" + (relatedEvent != null ? relatedEvent.getId() : "null") +
                ", locationId=" + (assignedLocation != null ? assignedLocation.getId() : "null") +
                ", taskCount=" + relatedTasks.size() +
                '}';
    }
}
