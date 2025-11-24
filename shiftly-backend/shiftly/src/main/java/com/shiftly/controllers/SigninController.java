package com.shiftly.controllers;

import com.shiftly.dto.SignInRequest;
import com.shiftly.dto.SignInResponse;
import com.shiftly.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/signin")
public class SigninController {

    private final AuthService authService;

    public SigninController(AuthService authService) {this.authService = authService;};

    @PostMapping
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request) {
        try {
            SignInResponse response = authService.signIn(request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            // Return 401 for any auth-related exception
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        } catch (Exception ex) {
            // Catch any other exception and return 401
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        }
    }

    private record ErrorResponse(String message) {}
}
