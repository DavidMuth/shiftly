package com.shiftly.models;

import java.sql.Timestamp;

public class Event {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    private String name;
    private String description;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;

    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }

    private boolean isBreak;


    public Event(int id, String name, String description, Timestamp startTimestamp, Timestamp endTimestamp, boolean isBreak) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.isBreak = isBreak;
    }

    public Event(){

    }
}
