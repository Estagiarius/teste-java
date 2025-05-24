package com.teacheragenda.repository;

import com.teacheragenda.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find all tasks, order by priority descending (High first), then by sortOrder ascending, then by due date ascending
    List<Task> findAllByOrderByPriorityDescSortOrderAscDueDateAsc();

    // Find all incomplete tasks, order by priority descending, then by sortOrder ascending, then by due date ascending
    List<Task> findAllByCompletedFalseOrderByPriorityDescSortOrderAscDueDateAsc();

    // Find all completed tasks, ordered by due date descending.
    // sortOrder might not be as relevant here, but could be added if needed.
    List<Task> findAllByCompletedTrueOrderByDueDateDesc();

    // You can add other specific queries if needed, for example:
    // List<Task> findByDescriptionContainingIgnoreCase(String keyword);
    // List<Task> findByDueDateBefore(LocalDate date);
}
