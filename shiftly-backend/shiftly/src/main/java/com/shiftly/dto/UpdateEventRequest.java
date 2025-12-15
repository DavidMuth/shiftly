package com.shiftly.dto;

import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;

public class UpdateEventRequest {
    @NotBlank(message = "Name is required")
    private String name;

    public int getUserId() {
        return userId;
    }
    @NotBlank(message = "User ID is required.")
    private int userId;

    public int getEventId() {
        return eventId;
    }
    @NotBlank(message = "Event ID is required.")
    private int eventId;

    private String description;

    @NotBlank(message = "Start Timestamp is required")
    private Timestamp startTimestamp;

    @NotBlank(message = "End Timestamp is required")
    private Timestamp endTimestamp;

    private boolean isBreak;

    public boolean getIsBreak() {
        return isBreak;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
