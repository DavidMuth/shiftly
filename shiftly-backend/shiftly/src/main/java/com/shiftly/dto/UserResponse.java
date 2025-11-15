package com.shiftly.dto;

public class UserResponse {
    private int id;
    private String name;
    private String email;
    private int workingHours;

    public UserResponse(int id, String name, String email, int workingHours) {
        this.id = id;
        this.name = name;
        this.email = email;
        if (workingHours == 0) {
            this.workingHours = 20;
        } else {
            this.workingHours = workingHours;
        }
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getWorkingHours() { return workingHours; }
}