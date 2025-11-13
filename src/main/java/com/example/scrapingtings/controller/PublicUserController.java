package com.example.scrapingtings.controller;

import com.example.scrapingtings.dto.UserRequest;
import com.example.scrapingtings.model.User;
import com.example.scrapingtings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicUserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest request) {
        try {
            User user = userService.createUser(request.username(), request.password());
            String successMessage = "User created successfully with ID: " + user.getId();
            return new ResponseEntity<>(successMessage, HttpStatus.CREATED);

        } catch(Exception e) {
            System.err.println("Error creating user " + e.getMessage());
            return new ResponseEntity<>("Failed to create and save user " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
