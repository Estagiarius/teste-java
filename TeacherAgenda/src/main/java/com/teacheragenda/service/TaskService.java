package com.teacheragenda.service;

import com.teacheragenda.model.Priority;
import com.teacheragenda.model.Task;
import com.teacheragenda.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task saveTask(Task task) {
        // When a task is saved, if sortOrder is null (e.g. new task not from D&D),
        // assign a default. A simple way is to count existing tasks with same priority.
        // This might not be perfect for all cases but is a starting point.
        // A better approach might be to set it based on the highest sortOrder for that priority + 1.
        if (task.getId() == null && task.getSortOrder() == null) {
             task.setSortOrder(0); // Default for new tasks, D&D will refine it
        }
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        // Use the new sorted method by default if it makes sense for general "getAll"
        return taskRepository.findAllByOrderByPriorityDescSortOrderAscDueDateAsc();
    }

    @Transactional(readOnly = true)
    public List<Task> getPendingTasksSorted() {
        return taskRepository.findAllByCompletedFalseOrderByPriorityDescSortOrderAscDueDateAsc();
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasksSorted() {
        return taskRepository.findAllByOrderByPriorityDescSortOrderAscDueDateAsc();
    }


    @Transactional
    public Optional<Task> markTaskAsCompleted(Long taskId, boolean completed) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setCompleted(completed);
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Task> updateTaskPriority(Long taskId, Priority newPriority) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setPriority(newPriority);
            // Potentially reset sortOrder or re-evaluate based on new priority
            // For now, keep existing sortOrder, D&D will adjust.
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Task> updateTask(Long taskId, String description, LocalDate dueDate, Priority priority, boolean completed) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setDescription(description);
            task.setDueDate(dueDate);
            // If priority changes here, sortOrder might need recalculation relative to new peers
            if (task.getPriority() != priority) {
                task.setPriority(priority);
                // Reset sortOrder or assign a default high value to be re-sorted later if needed
                // For simplicity, let D&D handle refined sortOrder changes
            }
            task.setCompleted(completed);
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
    }

    @Transactional
    public void updateTaskSortOrder(List<Task> reorderedTasks) {
        // This assumes reorderedTasks contains tasks of the SAME priority,
        // and their order in the list determines their new sortOrder within that priority.
        // Or, if it's a full list, it implies the priority might also change based on sections.
        // For this implementation, we'll update sortOrder based on list position.
        // It's crucial that the list provided is already sorted as desired by priority first,
        // then by the new drag-and-drop order.
        for (int i = 0; i < reorderedTasks.size(); i++) {
            Task task = reorderedTasks.get(i);
            Optional<Task> taskOptional = taskRepository.findById(task.getId());
            if (taskOptional.isPresent()) {
                Task dbTask = taskOptional.get();
                dbTask.setSortOrder(i); // Set sortOrder based on the new list index
                // dbTask.setPriority(task.getPriority()); // If priority can change too
                taskRepository.save(dbTask);
            }
        }
    }


    //<editor-fold desc="Statistics Methods">
    @Transactional(readOnly = true)
    public long getTotalTaskCount() {
        return taskRepository.count();
    }

    @Transactional(readOnly = true)
    public long getCompletedTaskCount() {
        return taskRepository.findAll().stream().filter(Task::isCompleted).count();
    }

    @Transactional(readOnly = true)
    public long getPendingTaskCount() {
        return taskRepository.findAll().stream().filter(task -> !task.isCompleted()).count();
    }

    @Transactional(readOnly = true)
    public Map<Priority, Long> getTaskCountByPriority() {
        return taskRepository.findAll().stream()
                .filter(task -> task.getPriority() != null) // Ensure priority is not null
                .collect(Collectors.groupingBy(Task::getPriority, EnumMap::new, Collectors.counting()));
    }
    //</editor-fold>
}
