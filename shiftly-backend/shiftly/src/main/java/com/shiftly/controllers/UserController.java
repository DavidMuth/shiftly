package com.shiftly.controllers;

import com.shiftly.models.User;
import com.shiftly.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @GetMapping("/{name}")
    public User getUserById(@PathVariable String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found with name " + name));
    }


    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User createdUser = userRepository.create(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PutMapping
    public String updateUser(@RequestBody User user) {
        return "updated user";
    }

    @DeleteMapping
    public String deleteUser(@RequestBody User user) {
        return "deleted user";
    }
}
