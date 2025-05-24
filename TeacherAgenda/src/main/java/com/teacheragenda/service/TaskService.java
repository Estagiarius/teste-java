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
        return taskRepository.findAll(); // Or a default sorted one
    }

    @Transactional(readOnly = true)
    public List<Task> getPendingTasksSorted() {
        return taskRepository.findAllByCompletedFalseOrderByPriorityDescDueDateAsc();
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasksSorted() {
        return taskRepository.findAllByOrderByPriorityDescDueDateAsc();
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
            task.setPriority(priority);
            task.setCompleted(completed);
            return Optional.of(taskRepository.save(task));
        }
        return Optional.empty();
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

    // Optional: More complex statistics can be added later
    // @Transactional(readOnly = true)
    // public Map<LocalDate, Long> getTasksCompletedByDate(LocalDate startDate, LocalDate endDate) {
    //     return taskRepository.findAll().stream()
    //             .filter(Task::isCompleted)
    //             .filter(task -> task.getDueDate() != null && !task.getDueDate().isBefore(startDate) && !task.getDueDate().isAfter(endDate))
    //             .collect(Collectors.groupingBy(Task::getDueDate, Collectors.counting()));
    // }
    //</editor-fold>
}
