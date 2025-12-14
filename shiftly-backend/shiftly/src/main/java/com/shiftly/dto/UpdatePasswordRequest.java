package com.shiftly.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdatePasswordRequest {
    @NotBlank(message = "Current Password is required")
    private String currentPassword;

    @NotBlank(message = "New Password is required")
    private String newPassword;

    @NotBlank(message = "New Password confirmation is required")
    private String confirmNewPassword;

    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }
}
