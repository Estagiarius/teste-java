package com.teacheragenda.service;

import com.teacheragenda.model.Event;
import com.teacheragenda.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> findEventsByMonth(YearMonth yearMonth) {
        LocalDateTime startTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return eventRepository.findAllByStartTimeBetween(startTime, endTime);
    }

    public List<Event> findEventsByDay(LocalDate date) {
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(23, 59, 59, 999_999_999); // End of the day
        return eventRepository.findAllByStartTimeBetween(startTime, endTime);
    }

    public List<Event> findEventsBetween(LocalDateTime weekStart, LocalDateTime weekEnd) {
        return eventRepository.findAllByStartTimeBetween(weekStart, weekEnd);
    }
}
