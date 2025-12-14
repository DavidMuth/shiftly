package com.shiftly.controllers;

import com.shiftly.dto.CreateUserRequest;
import com.shiftly.dto.UpdatePasswordRequest;
import com.shiftly.dto.UpdateWorkingHoursRequest;
import com.shiftly.dto.UserResponse;
import com.shiftly.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shiftly/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
        int userId = (int) request.getAttribute("userId");
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/me/updateWorkingHours")
    public ResponseEntity<UserResponse> updateCurrentUserWorkingHours(@Valid @RequestBody UpdateWorkingHoursRequest request, HttpServletRequest httpRequest) {
        int userId = (int) httpRequest.getAttribute("userId");
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponse response = userService.updateUserWorkingHours(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/me/updatePassword")
    public ResponseEntity<UserResponse> updateCurrentUserPassword(@Valid @RequestBody UpdatePasswordRequest request, HttpServletRequest httpRequest) {
        int userId = (int) httpRequest.getAttribute("userId");
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponse response = userService.updateUserPassword(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}