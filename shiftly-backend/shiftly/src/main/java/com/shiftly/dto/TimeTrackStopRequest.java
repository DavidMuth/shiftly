package com.shiftly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public class TimeTrackStopRequest {
    @NotNull(message = "Event Id is required")
    private int eventId;

    @NotNull(message = "End Timestamp is required")
    private Timestamp endTimestamp;

    public int getEventId() {return eventId;}
    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }
}
