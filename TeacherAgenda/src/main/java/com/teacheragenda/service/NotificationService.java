package com.teacheragenda.service;

import com.teacheragenda.model.Event;
import com.teacheragenda.model.Task;
import com.teacheragenda.repository.EventRepository;
import com.teacheragenda.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter EVENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TASK_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final EmailService emailService;
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public NotificationService(EmailService emailService, EventRepository eventRepository, TaskRepository taskRepository) {
        this.emailService = emailService;
        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Sends reminders for upcoming events.
     * "Upcoming" is defined as events starting between now and 24 hours from now.
     * @return Number of event reminders sent.
     */
    public int sendEventReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in24Hours = now.plusHours(24);
        List<Event> upcomingEvents = eventRepository.findAllByStartTimeBetween(now, in24Hours);

        if (upcomingEvents.isEmpty()) {
            logger.info("No upcoming events found for reminders.");
            return 0;
        }

        int remindersSent = 0;
        for (Event event : upcomingEvents) {
            String subject = "Event Reminder: " + event.getTitle();
            String textBody = String.format(
                "This is a reminder for your event: '%s'.\n" +
                "Scheduled Start Time: %s\n" +
                "Location: %s\n" +
                "Description: %s",
                event.getTitle(),
                event.getStartTime().format(EVENT_TIME_FORMATTER),
                event.getLocation() != null ? event.getLocation() : "N/A",
                event.getDescription() != null ? event.getDescription() : "N/A"
            );

            boolean success = emailService.sendNotification(subject, textBody);
            if (success) {
                remindersSent++;
            }
        }
        logger.info("Attempted to send {} event reminders. Successfully sent: {}", upcomingEvents.size(), remindersSent);
        return remindersSent;
    }

    /**
     * Sends reminders for tasks that are due today or overdue and are not yet completed.
     * @return Number of task reminders sent.
     */
    public int sendTaskReminders() {
        LocalDate today = LocalDate.now();
        // Fetch tasks that are not completed and are due on or before today.
        // The TaskRepository method findAllByCompletedFalseOrderByPriorityDescDueDateAsc does not filter by due date.
        // We will filter in memory, or ideally, add a new repository method.
        // For now, let's fetch pending tasks and filter.
        List<Task> pendingTasks = taskRepository.findAllByCompletedFalseOrderByPriorityDescDueDateAsc();

        List<Task> tasksToRemind = pendingTasks.stream()
            .filter(task -> task.getDueDate() != null && !task.getDueDate().isAfter(today))
            .toList();

        if (tasksToRemind.isEmpty()) {
            logger.info("No tasks found for reminders (due today or overdue).");
            return 0;
        }

        int remindersSent = 0;
        for (Task task : tasksToRemind) {
            String subject = "Task Reminder: " + task.getDescription();
            String textBody = String.format(
                "This is a reminder for your task: '%s'.\n" +
                "Due Date: %s\n" +
                "Priority: %s\n" +
                "%s",
                task.getDescription(),
                task.getDueDate().format(TASK_DATE_FORMATTER),
                task.getPriority().getDisplayName(),
                task.getDueDate().isBefore(today) ? "This task is OVERDUE." : "This task is due today."
            );

            boolean success = emailService.sendNotification(subject, textBody);
            if (success) {
                remindersSent++;
            }
        }
        logger.info("Attempted to send {} task reminders. Successfully sent: {}", tasksToRemind.size(), remindersSent);
        return remindersSent;
    }
}
