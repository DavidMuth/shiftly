package com.shiftly.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private int workingHours;

    public User() {}
    public User(int id, String name, String email, String passwordHash, int workingHours) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.workingHours = workingHours;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public int getWorkingHours() { return workingHours; }

    public String getPasswordHash() { return passwordHash; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setWorkingHours(int hours) { this.workingHours = hours; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
