package com.teacheragenda.service;

import com.teacheragenda.model.Activity;
import com.teacheragenda.model.Event;
import com.teacheragenda.model.Location;
import com.teacheragenda.model.Task;
import com.teacheragenda.repository.ActivityRepository;
import com.teacheragenda.repository.EventRepository;
import com.teacheragenda.repository.LocationRepository;
import com.teacheragenda.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository,
                           EventRepository eventRepository,
                           TaskRepository taskRepository,
                           LocationRepository locationRepository) {
        this.activityRepository = activityRepository;
        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public Activity saveActivity(Activity activity, Long eventId, List<Long> taskIds, Long locationId) {
        // Set related Event
        if (eventId != null) {
            Optional<Event> eventOptional = eventRepository.findById(eventId);
            eventOptional.ifPresent(activity::setRelatedEvent);
            // Or throw an exception if event not found and it's mandatory
            // if (eventOptional.isEmpty()) { throw new EntityNotFoundException("Event not found with ID: " + eventId); }
        } else {
            activity.setRelatedEvent(null);
        }

        // Set related Tasks
        if (taskIds != null && !taskIds.isEmpty()) {
            List<Task> tasks = taskRepository.findAllById(taskIds);
            activity.setRelatedTasks(new HashSet<>(tasks));
        } else {
            activity.setRelatedTasks(new HashSet<>());
        }

        // Set assigned Location
        if (locationId != null) {
            Optional<Location> locationOptional = locationRepository.findById(locationId);
            locationOptional.ifPresent(activity::setAssignedLocation);
            // Or throw an exception if location not found and it's mandatory
            // if (locationOptional.isEmpty()) { throw new EntityNotFoundException("Location not found with ID: " + locationId); }
        } else {
            activity.setAssignedLocation(null);
        }

        return activityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long activityId) {
        // Consider implications: Does deleting an activity affect related tasks or events?
        // For now, simple deletion.
        // Also, ensure that the activity exists before trying to delete.
        if (!activityRepository.existsById(activityId)) {
            // throw new EntityNotFoundException("Activity not found with ID: " + activityId);
            return; // Or handle as appropriate
        }
        activityRepository.deleteById(activityId);
    }

    @Transactional(readOnly = true)
    public List<Activity> getAllActivities() {
        // return activityRepository.findAll(); // Basic retrieval
        return activityRepository.findAllWithEventAndLocationSortedByScheduledTimeDesc(); // More informative
    }

    @Transactional(readOnly = true)
    public Optional<Activity> getActivityById(Long activityId) {
        // Fetch with related entities to avoid lazy initialization issues in the controller/view
        // This can be done with a custom query in the repository or by "touching" the collections.
        // For simplicity, if using Open Session In View, direct access might work, but it's often better to be explicit.
        Optional<Activity> activity = activityRepository.findById(activityId);
        activity.ifPresent(act -> {
            // Initialize lazy collections if needed and not using FETCH JOIN in repository query
            if (act.getRelatedEvent() != null) act.getRelatedEvent().getTitle(); // Example access
            if (act.getAssignedLocation() != null) act.getAssignedLocation().getName(); // Example access
            act.getRelatedTasks().size(); // Access size to initialize task collection
        });
        return activity;
    }

    // Consider adding methods to update specific aspects, e.g.,
    // updateActivityTasks(Long activityId, List<Long> taskIds)
    // updateActivityLocation(Long activityId, Long locationId)
    // etc.
}
