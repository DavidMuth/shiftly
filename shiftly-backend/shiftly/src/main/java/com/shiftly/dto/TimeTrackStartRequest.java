package com.shiftly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public class TimeTrackStartRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Start Timestamp is required")
    private Timestamp startTimestamp;

    private boolean isBreak;

    public boolean getIsBreak() {
        return isBreak;
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
