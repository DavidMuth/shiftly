package com.shiftly.controllers;


import com.shiftly.dto.CreateUserRequest;
import com.shiftly.dto.SignUpRequest;
import com.shiftly.dto.SignUpResponse;
import com.shiftly.dto.UserResponse;
import com.shiftly.exceptions.UserAlreadyExistsException;
import com.shiftly.exceptions.ValidationException;
import com.shiftly.services.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {this.userService = userService;}

    @PostMapping
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        if (!request.passwordsMatch()) {
            return ResponseEntity.badRequest()
                    .body(new SignUpResponse(false, "Passwords do not match"));
        }

        try {
            CreateUserRequest createUserRequest = new CreateUserRequest();
            createUserRequest.setName(request.getName());
            createUserRequest.setEmail(request.getEmail());
            createUserRequest.setPassword(request.getPassword());
            createUserRequest.setConfirmPassword(request.getConfirmPassword());
            createUserRequest.setWorkingHours(20);

            UserResponse createdUser = userService.createUser(createUserRequest);

            return ResponseEntity.ok()
                    .body(new SignUpResponse(true, "User created successfully"));

        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new SignUpResponse(false, e.getMessage()));

        } catch (ValidationException e) {
            return ResponseEntity.badRequest()
                    .body(new SignUpResponse(false, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new SignUpResponse(false, "An unexpected error occurred"));
        }
    }
}