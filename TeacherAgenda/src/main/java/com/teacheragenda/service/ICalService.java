package com.teacheragenda.service;

import com.teacheragenda.model.Event;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.validate.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ICalService {

    private static final Logger logger = LoggerFactory.getLogger(ICalService.class);

    public ICalService() {
        // Default constructor
    }

    public boolean exportEventsToICS(List<Event> events, Path filePath) {
        if (events == null || events.isEmpty()) {
            logger.info("No events provided for export.");
            return false;
        }
        if (filePath == null) {
            logger.error("File path for export is null.");
            return false;
        }

        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//TeacherAgenda//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        for (Event appEvent : events) {
            try {
                LocalDateTime startLdt = appEvent.getStartTime();
                LocalDateTime endLdt = appEvent.getEndTime();

                DateTime start = new DateTime(java.util.Date.from(startLdt.atZone(ZoneId.systemDefault()).toInstant()));
                DateTime end = new DateTime(java.util.Date.from(endLdt.atZone(ZoneId.systemDefault()).toInstant()));

                VEvent vEvent = new VEvent(start, end, appEvent.getTitle());
                vEvent.getProperties().add(new Uid(UUID.randomUUID().toString()));

                if (appEvent.getDescription() != null && !appEvent.getDescription().isEmpty()) {
                    vEvent.getProperties().add(new Description(appEvent.getDescription()));
                }
                if (appEvent.getLocation() != null && !appEvent.getLocation().isEmpty()) {
                    vEvent.getProperties().add(new Location(appEvent.getLocation()));
                }
                calendar.getComponents().add(vEvent);
            } catch (Exception e) {
                logger.error("Error creating VEvent for event ID {}: {}", appEvent.getId(), e.getMessage(), e);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, fos);
            logger.info("Events successfully exported to: {}", filePath);
            return true;
        } catch (IOException | ValidationException e) {
            logger.error("Exception during iCalendar export to {}: {}", filePath, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Imports events from an iCalendar (.ics) file.
     *
     * @param filePath The Path of the .ics file to import.
     * @return A list of Event objects parsed from the file. Returns empty list on error or if no events found.
     */
    public List<Event> importEventsFromICS(Path filePath) {
        List<Event> importedEvents = new ArrayList<>();
        if (filePath == null) {
            logger.error("File path for import is null.");
            return importedEvents;
        }

        try (FileInputStream fin = new FileInputStream(filePath.toFile())) {
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);

            for (Component component : calendar.getComponents(Component.VEVENT)) {
                if (component instanceof VEvent) {
                    VEvent vEvent = (VEvent) component;
                    Event appEvent = new Event();

                    // Title (SUMMARY)
                    Optional<Summary> summary = vEvent.getProperty(Property.SUMMARY);
                    summary.ifPresent(s -> appEvent.setTitle(s.getValue()));

                    // Start Time (DTSTART)
                    Optional<DtStart> dtStart = vEvent.getProperty(Property.DTSTART);
                    if (dtStart.isPresent()) {
                        // Convert ical4j Date to LocalDateTime.
                        // This assumes the date/time in ics is either UTC or floating,
                        // and converts to system default timezone.
                        // For more robust timezone handling, ical4j's TimeZone and TimeZoneRegistry
                        // would be needed to interpret TZID parameters if present.
                        Instant startInstant = dtStart.get().getDate().toInstant();
                        appEvent.setStartTime(LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()));
                    } else {
                        logger.warn("Skipping VEvent without DTSTART: {}", summary.map(Summary::getValue).orElse("N/A"));
                        continue; // Skip event if no start date
                    }

                    // End Time (DTEND or DURATION)
                    Optional<DtEnd> dtEnd = vEvent.getProperty(Property.DTEND);
                    Optional<Duration> durationProp = vEvent.getProperty(Property.DURATION);

                    if (dtEnd.isPresent()) {
                        Instant endInstant = dtEnd.get().getDate().toInstant();
                        appEvent.setEndTime(LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()));
                    } else if (durationProp.isPresent() && appEvent.getStartTime() != null) {
                        // Calculate DTEND from DTSTART and DURATION
                        java.time.Duration javaDuration = java.time.Duration.parse(durationProp.get().getValue());
                        appEvent.setEndTime(appEvent.getStartTime().plus(javaDuration));
                    } else if (appEvent.getStartTime() != null) {
                        // If no DTEND and no DURATION, make it a 1-hour event by default (or handle as error)
                        logger.warn("VEvent missing DTEND and DURATION. Defaulting to 1 hour duration for: {}", appEvent.getTitle());
                        appEvent.setEndTime(appEvent.getStartTime().plusHours(1));
                    } else {
                         logger.warn("Skipping VEvent without sufficient end time information: {}", summary.map(Summary::getValue).orElse("N/A"));
                        continue; // Skip if end time cannot be determined
                    }

                    // Validate that EndTime is after StartTime
                    if (appEvent.getEndTime().isBefore(appEvent.getStartTime())) {
                        logger.warn("Skipping VEvent where end time is before start time: {}", appEvent.getTitle());
                        continue;
                    }


                    // Description
                    Optional<Description> description = vEvent.getProperty(Property.DESCRIPTION);
                    description.ifPresent(d -> appEvent.setDescription(d.getValue()));

                    // Location
                    Optional<Location> location = vEvent.getProperty(Property.LOCATION);
                    location.ifPresent(l -> appEvent.setLocation(l.getValue()));

                    // UID - Not storing in model for now, but can be logged or used for advanced duplicate checks
                    Optional<Uid> uid = vEvent.getProperty(Property.UID);
                    uid.ifPresent(u -> logger.debug("Imported VEvent with UID: {}", u.getValue()));

                    importedEvents.add(appEvent);
                }
            }
            logger.info("Successfully parsed {} events from: {}", importedEvents.size(), filePath);

        } catch (IOException e) {
            logger.error("IOException during iCalendar import from {}: {}", filePath, e.getMessage(), e);
        } catch (ParserException e) {
            logger.error("ParserException during iCalendar import from {}: {}", filePath, e.getMessage(), e);
        }
        return importedEvents;
    }
}
