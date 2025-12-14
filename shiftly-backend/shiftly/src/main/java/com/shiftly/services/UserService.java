package com.shiftly.services;

import com.shiftly.config.SecurityConfig;
import com.shiftly.dto.CreateUserRequest;
import com.shiftly.dto.UpdatePasswordRequest;
import com.shiftly.dto.UpdateWorkingHoursRequest;
import com.shiftly.dto.UserResponse;
import com.shiftly.exceptions.UserAlreadyExistsException;
import com.shiftly.models.User;
import com.shiftly.repositories.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        // create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setWorkingHours(request.getWorkingHours());

        User savedUser = userRepository.create(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getWorkingHours()
        );
    }

    public UserResponse updateUserWorkingHours(UpdateWorkingHoursRequest request, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        user.setWorkingHours(request.getWorkingHours());

        User updatedUser = userRepository.updateWorkingHours(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getWorkingHours()
        );
    }

    public UserResponse updateUserPassword(UpdatePasswordRequest request, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        String passwordHash = getUserPasswordHash(user.getEmail());
        if (!SecurityConfig.passwordEncoder().matches(request.getCurrentPassword(), passwordHash)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid password"
            );
        }

        if (!request.passwordsMatch()) {
            throw  new RuntimeException("New passwords do not match.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        User updatedUser = userRepository.updatePassword(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getWorkingHours()
        );
    }

    public UserResponse getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getWorkingHours());
    }

    public UserResponse getUserByName(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found with name " + name));

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getWorkingHours());
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));

        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getWorkingHours());
    }

    public String getUserPasswordHash(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));

        return user.getPasswordHash();
    }
}
