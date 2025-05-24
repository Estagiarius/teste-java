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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter EVENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TASK_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Keep EmailService for now, controller will decide whether to send email
    private final EmailService emailService;
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;

    // Simple data class for notification content
    public static class DesktopNotification {
        private final String title;
        private final String message;
        private final String emailSubject; // For email sending
        private final String emailBody;    // For email sending


        public DesktopNotification(String title, String message, String emailSubject, String emailBody) {
            this.title = title;
            this.message = message;
            this.emailSubject = emailSubject;
            this.emailBody = emailBody;
        }

        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getEmailSubject() { return emailSubject; }
        public String getEmailBody() { return emailBody; }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DesktopNotification that = (DesktopNotification) o;
            return Objects.equals(title, that.title) && Objects.equals(message, that.message) && Objects.equals(emailSubject, that.emailSubject) && Objects.equals(emailBody, that.emailBody);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, message, emailSubject, emailBody);
        }
    }

    @Autowired
    public NotificationService(EmailService emailService, EventRepository eventRepository, TaskRepository taskRepository) {
        this.emailService = emailService;
        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Generates DesktopNotification objects for upcoming events.
     * "Upcoming" is defined as events starting between now and 24 hours from now.
     * @return List of DesktopNotification objects.
     */
    public List<DesktopNotification> generateEventRemindersContent() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in24Hours = now.plusHours(24);
        List<Event> upcomingEvents = eventRepository.findAllByStartTimeBetween(now, in24Hours);
        List<DesktopNotification> notifications = new ArrayList<>();

        if (upcomingEvents.isEmpty()) {
            logger.info("No upcoming events found for reminders.");
            return notifications;
        }

        for (Event event : upcomingEvents) {
            String title = "Event Reminder: " + event.getTitle();
            String message = String.format(
                "Event: '%s'\nStarts: %s\nLocation: %s",
                event.getTitle(),
                event.getStartTime().format(EVENT_TIME_FORMATTER),
                event.getLocation() != null ? event.getLocation() : "N/A"
            );
            String emailSubject = title;
            String emailBody = String.format(
                "This is a reminder for your event: '%s'.\n" +
                "Scheduled Start Time: %s\n" +
                "Location: %s\n" +
                "Description: %s",
                event.getTitle(),
                event.getStartTime().format(EVENT_TIME_FORMATTER),
                event.getLocation() != null ? event.getLocation() : "N/A",
                event.getDescription() != null ? event.getDescription() : "N/A"
            );
            notifications.add(new DesktopNotification(title, message, emailSubject, emailBody));
        }
        logger.info("Generated {} event reminder contents.", notifications.size());
        return notifications;
    }

    /**
     * Generates DesktopNotification objects for tasks that are due today or overdue and are not yet completed.
     * @return List of DesktopNotification objects.
     */
    public List<DesktopNotification> generateTaskRemindersContent() {
        LocalDate today = LocalDate.now();
        List<Task> pendingTasks = taskRepository.findAllByCompletedFalseOrderByPriorityDescSortOrderAscDueDateAsc();
        List<DesktopNotification> notifications = new ArrayList<>();

        List<Task> tasksToRemind = pendingTasks.stream()
            .filter(task -> task.getDueDate() != null && !task.getDueDate().isAfter(today))
            .toList();

        if (tasksToRemind.isEmpty()) {
            logger.info("No tasks found for reminders (due today or overdue).");
            return notifications;
        }

        for (Task task : tasksToRemind) {
            String title = (task.getDueDate().isBefore(today) ? "OVERDUE Task: " : "Task Due Today: ") + task.getDescription();
            String message = String.format(
                "Task: '%s'\nDue: %s\nPriority: %s",
                task.getDescription(),
                task.getDueDate().format(TASK_DATE_FORMATTER),
                task.getPriority().getDisplayName()
            );
             String emailSubject = "Task Reminder: " + task.getDescription();
             String emailBody = String.format(
                "This is a reminder for your task: '%s'.\n" +
                "Due Date: %s\n" +
                "Priority: %s\n" +
                "%s",
                task.getDescription(),
                task.getDueDate().format(TASK_DATE_FORMATTER),
                task.getPriority().getDisplayName(),
                task.getDueDate().isBefore(today) ? "This task is OVERDUE." : "This task is due today."
            );
            notifications.add(new DesktopNotification(title, message, emailSubject, emailBody));
        }
        logger.info("Generated {} task reminder contents.", notifications.size());
        return notifications;
    }
    
    // The actual email sending can still be a utility of this service if desired,
    // or entirely managed by the controller based on the DesktopNotification content.
    public boolean sendEmailNotification(DesktopNotification notification) {
        return emailService.sendNotification(notification.getEmailSubject(), notification.getEmailBody());
    }
}
