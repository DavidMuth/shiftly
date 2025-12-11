package com.shiftly.dto;


import com.shiftly.models.Event;

import java.sql.Timestamp;

public class EventResponse {
    private int eventId;
    private String name;
    private String description;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private boolean isBreak;

    public EventResponse(int eventId, String name, String description, Timestamp startTimestamp, Timestamp endTimestamp, boolean isBreak) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.isBreak = isBreak;
    }

    public EventResponse(Event event) {
        this.eventId = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.startTimestamp = event.getStartTimestamp();
        this.endTimestamp = event.getEndTimestamp();
        this.isBreak = event.isBreak();
    }

    public int getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }
    public boolean isBreak() {
        return isBreak;
    }
}
