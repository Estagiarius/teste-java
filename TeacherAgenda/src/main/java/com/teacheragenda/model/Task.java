package com.teacheragenda.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "task", indexes = { // Adding index for sorting
    @Index(name = "idx_task_priority_sortorder_duedate", columnList = "priority, sortOrder, dueDate")
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String description;

    private LocalDate dueDate;

    @Enumerated(EnumType.ORDINAL) // Stores LOW=0, MEDIUM=1, HIGH=2
    private Priority priority;

    @Column(nullable = false) // Default to a value to avoid null issues in sorting
    private Integer sortOrder = 0; // New field for fine-grained sorting within a priority

    private boolean completed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @ManyToMany(mappedBy = "relatedTasks", fetch = FetchType.LAZY)
    private Set<Activity> activities = new HashSet<>();

    // Constructors
    public Task() {
        this.creationDate = LocalDateTime.now();
    }

    public Task(String description, LocalDate dueDate, Priority priority) {
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
        this.creationDate = LocalDateTime.now();
        // Initialize sortOrder based on priority by default, or some other logic
        if (priority != null) {
            this.sortOrder = priority.ordinal(); // Example default, can be refined
        } else {
            this.sortOrder = Priority.MEDIUM.ordinal(); // Default if no priority given
        }
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public Set<Activity> getActivitiesInternal() {
        return activities;
    }

    public void setActivitiesInternal(Set<Activity> activities) {
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
                ", sortOrder=" + sortOrder +
                ", completed=" + completed +
                ", creationDate=" + creationDate +
                '}';
    }
}
