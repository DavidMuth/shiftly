package com.shiftly.controllers;

import com.shiftly.dto.CreateEventRequest;
import com.shiftly.dto.EventResponse;
import com.shiftly.dto.UpdateEventRequest;
import com.shiftly.dto.UserResponse;
import com.shiftly.services.EventService;
import com.sun.jdi.request.EventRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shiftly/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/get/{userId}")
    public ResponseEntity<List<EventResponse>> getEventsFromUser(@PathVariable int userId) {
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(eventService.getEventsFromUser(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Boolean> createEvent(@RequestBody CreateEventRequest eventRequest) {
        if (eventRequest.getUserId() == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(eventService.createEvent(eventRequest));
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> editEvent(@RequestBody UpdateEventRequest eventRequest) {
        if (eventRequest.getUserId() == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(eventService.editEvent(eventRequest));
    }
}
