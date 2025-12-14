package com.shiftly.dto;

import jakarta.validation.constraints.Min;

public class UpdateWorkingHoursRequest {
    @Min(value = 0, message = "Working hours cannot be negative")
    private int workingHours;

    public int getWorkingHours() {
        if (this.workingHours == 0) {
            workingHours = 20;
        }
        return workingHours;
    }
}
