package com.example.scrapingtings.controller;

import com.example.scrapingtings.dto.UserRequest;
import com.example.scrapingtings.model.User;
import com.example.scrapingtings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicUserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserRequest request) {
        try {
            User user = userService.createUser(request.username(), request.password());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "User created successfully");
            responseBody.put("id", user.getId());

            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Error creating user " + e.getMessage());

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Failed to create user");
            errorBody.put("details", e.getMessage());

            return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
