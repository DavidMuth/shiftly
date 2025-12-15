package com.shiftly.services;

import com.shiftly.dto.*;
import com.shiftly.exceptions.UserAlreadyExistsException;
import com.shiftly.models.Event;
import com.shiftly.models.User;
import com.shiftly.repositories.EventRepository;
import jakarta.validation.ValidationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;


    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventResponse> getEventsFromUser(int userId) {
        List<Event> events = eventRepository.getEventsFromUser(userId)
                .orElseThrow(() -> new RuntimeException("No events found for user " + userId));

        // Liste von Event in EventResponse umwandeln
        return events.stream()
                .map(EventResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Event getEventById(int userId, int eventId) {
        return eventRepository.getEventById(userId, eventId)
                .orElseThrow(() -> new RuntimeException("Event not found " + eventId));
    }

    @Transactional
    public boolean createEvent(CreateEventRequest request) {
        // create event
        Event event = new Event();
        event.setName(request.getName());
        event.setBreak(request.getIsBreak());
        event.setDescription(request.getDescription());
        event.setStartTimestamp(request.getStartTimestamp());
        event.setEndTimestamp(request.getEndTimestamp());
        event.setUserId(request.getUserId());

        return eventRepository.create(event);
    }

    public Boolean editEvent(UpdateEventRequest request) {
        // create event
        Event event = new Event();
        event.setName(request.getName());
        event.setBreak(request.getIsBreak());
        event.setDescription(request.getDescription());
        event.setStartTimestamp(request.getStartTimestamp());
        event.setEndTimestamp(request.getEndTimestamp());
        event.setUserId(request.getUserId());
        event.setId(request.getEventId());

        return eventRepository.edit(event);
    }
  
    public Boolean deleteEvent(int eventId) {
        return eventRepository.delete(eventId);
    }

    
    @Transactional
    public int startEvent(TimeTrackStartRequest request, int userId) {
        Event event = new Event();
        event.setName(request.getName());
        event.setBreak(request.getIsBreak());
        event.setDescription(request.getDescription());
        event.setStartTimestamp(request.getStartTimestamp());
        event.setUserId(userId);

        return eventRepository.startEvent(event);
    }
  
    @Transactional
    public int stopEvent(TimeTrackStopRequest request, int userId) {
        Event existingEvent = getEventById(userId, request.getEventId());
        if (existingEvent == null) {
            throw new ResourceNotFoundException("Event not found or does not belong to user");
        }

        Event event = new Event();
        event.setUserId(userId);
        event.setId(request.getEventId());
        event.setEndTimestamp(request.getEndTimestamp());

        int rowsAffected = eventRepository.stopEvent(event);

        if (rowsAffected == 0) {
            throw new ResourceNotFoundException("Failed to stop event");
        }

        return request.getEventId();
    }
}
