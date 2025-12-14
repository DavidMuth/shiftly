package com.shiftly.services;

import com.shiftly.dto.*;
import com.shiftly.exceptions.UserAlreadyExistsException;
import com.shiftly.models.Event;
import com.shiftly.models.User;
import com.shiftly.repositories.EventRepository;
import jakarta.validation.ValidationException;
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
}
