package com.shiftly.services;

import com.shiftly.config.SecurityConfig;
import com.shiftly.dto.SignInRequest;
import com.shiftly.dto.SignInResponse;
import com.shiftly.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserService userAuthService;

    public AuthService(UserService userAuthService) {
        this.userAuthService = userAuthService;
    }

    public SignInResponse signIn(SignInRequest request) {
        String passwordHash = userAuthService.getUserPasswordHash(request.getEmail());

        if (!SecurityConfig.passwordEncoder().matches(request.getPassword(), passwordHash)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid password"
            );
        }

        UserResponse user = userAuthService.getUserByEmail(request.getEmail());

        String token = JwtService.generateToken(user.getId(), user.getEmail());

        return new SignInResponse(
                token
        );
    }
}
