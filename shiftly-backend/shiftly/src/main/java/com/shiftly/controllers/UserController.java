package com.shiftly.shiftly.controllers;

import com.shiftly.shiftly.models.UserModel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping
    public void getUser() {

    }

    @PostMapping
    public String createUser(@RequestBody UserModel user) {
        // The user object is automatically populated from JSON in the request body
        return "Received user: " + user.getName();
    }

    @PutMapping
    public String updateUser(@RequestBody UserModel user) {
        return "updated user";
    }

    @DeleteMapping
    public String deleteUser(@RequestBody UserModel user) {
        return "deleted user";
    }
}
