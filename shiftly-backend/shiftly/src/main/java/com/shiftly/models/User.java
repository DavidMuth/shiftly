package com.shiftly.models;

public class UserModel {
    private int id;
    private String name;
    private int workingHours;

    public UserModel() {};

    public String getName() {
        return name;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkingHours(int hours) {
        this.workingHours = hours;
    }
}
