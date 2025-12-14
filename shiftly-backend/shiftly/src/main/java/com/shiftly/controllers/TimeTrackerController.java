package com.shiftly.controllers;

import com.shiftly.dto.TimeTrackStartRequest;
import com.shiftly.dto.TimeTrackStopRequest;
import com.shiftly.services.EventService;
import com.shiftly.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shiftly/timeTracker")
public class TimeTrackerController {

    private final EventService eventService;

    public TimeTrackerController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/start")
    public ResponseEntity<Integer> startTracking(@Valid @RequestBody TimeTrackStartRequest request, HttpServletRequest httpRequest) {
        int userId = (int) httpRequest.getAttribute("userId");
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int eventId = eventService.startEvent(request, userId);
        return ResponseEntity.ok(eventId);
    }

    @PutMapping("/stop")
    public ResponseEntity<Integer> stopTracking(@Valid @RequestBody TimeTrackStopRequest request, HttpServletRequest httpRequest) {
        int userId = (int) httpRequest.getAttribute("userId");
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int eventId = eventService.stopEvent(request, userId);
        return ResponseEntity.ok(eventId);
    }
}
