package com.shiftly.controllers;

import com.shiftly.services.JwtService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifyJwtController {

    private final JwtService jwtService = new JwtService();

    
}
