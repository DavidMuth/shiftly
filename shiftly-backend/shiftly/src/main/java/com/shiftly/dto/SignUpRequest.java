package com.shiftly.dto;

import jakarta.validation.constraints.NotBlank;

public class SignUpRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }

    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getConfirmPassword() {return confirmPassword;}
    public boolean getPasswordMatch() {return passwordsMatch();}
}
