package com.shiftly.dto;

import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;

public class CreateEventRequest {
    @NotBlank(message = "Name is required")
    private String name;

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
