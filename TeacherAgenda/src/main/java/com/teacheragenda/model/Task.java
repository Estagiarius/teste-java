package com.teacheragenda.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.ORDINAL) // Store enum as integer (its ordinal value)
    private Priority priority;

    private boolean completed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @ManyToMany(mappedBy = "relatedTasks", fetch = FetchType.LAZY)
    private Set<Activity> activities = new HashSet<>();

    // Constructors
    public Task() {
        this.creationDate = LocalDateTime.now(); // Auto-set on creation
    }

    public Task(String description, LocalDate dueDate, Priority priority) {
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
        this.creationDate = LocalDateTime.now(); // Auto-set on creation
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    // Setter for creationDate is typically not needed if auto-set
    // public void setCreationDate(LocalDateTime creationDate) {
    //     this.creationDate = creationDate;
    // }

    public Set<Activity> getActivitiesInternal() { // Renamed to avoid conflict if a direct getter 'getActivities' is desired
        return activities;
    }

    public void setActivitiesInternal(Set<Activity> activities) { // Renamed
        this.activities = activities;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", completed=" + completed +
                ", creationDate=" + creationDate +
                '}';
    }
}
