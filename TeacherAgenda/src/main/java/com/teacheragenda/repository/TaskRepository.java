package com.teacheragenda.repository;

import com.teacheragenda.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find all tasks, order by priority descending (High first), then by due date ascending (earliest first)
    List<Task> findAllByOrderByPriorityDescDueDateAsc();

    // Find all incomplete tasks, order by priority descending, then by due date ascending
    List<Task> findAllByCompletedFalseOrderByPriorityDescDueDateAsc();

    // Find all completed tasks
    List<Task> findAllByCompletedTrueOrderByDueDateDesc();

    // You can add other specific queries if needed, for example:
    // List<Task> findByDescriptionContainingIgnoreCase(String keyword);
    // List<Task> findByDueDateBefore(LocalDate date);
}
