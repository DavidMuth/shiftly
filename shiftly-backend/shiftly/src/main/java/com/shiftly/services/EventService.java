package com.shiftly.services;

import com.shiftly.dto.EventResponse;
import com.shiftly.dto.UserResponse;
import com.shiftly.models.Event;
import com.shiftly.models.User;
import com.shiftly.repositories.EventRepository;
import org.springframework.stereotype.Service;

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

}
