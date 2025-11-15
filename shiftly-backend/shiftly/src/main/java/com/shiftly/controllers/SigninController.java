package com.shiftly.controllers;

import com.shiftly.dto.CreateUserRequest;
import com.shiftly.dto.SignInRequest;
import com.shiftly.dto.SignInResponse;
import com.shiftly.dto.UserResponse;
import com.shiftly.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signin")
public class SigninController {

    private final AuthService authService;

    public SigninController(AuthService authService) {this.authService = authService;};

    @PostMapping
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        SignInResponse response = authService.signIn(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
