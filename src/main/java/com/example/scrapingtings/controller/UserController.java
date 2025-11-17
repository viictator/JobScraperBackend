package com.example.scrapingtings.controller;

import com.example.scrapingtings.dto.ProfileUpdateRequest;
import com.example.scrapingtings.dto.UserDto;
import com.example.scrapingtings.model.User;
import com.example.scrapingtings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("")
    public ResponseEntity<User> getUser(Principal principal) {
        String username = principal.getName();

        try {
            User user = userService.getUserByUsername(username);

            // 3. Security Step: Remove the sensitive password hash
            user.setPassword(null);

            user.setPersonalName(user.getPersonalName() == null ? "John Doe" : user.getPersonalName());
            user.setPersonalEmail(user.getPersonalEmail() == null ? "John@Doe.com" : user.getPersonalEmail());
            user.setPersonalAddress(user.getPersonalAddress() == null ? "John Doe 42nd Street" : user.getPersonalAddress());
            user.setProfileText(user.getProfileText() == null ? "" : user.getProfileText());

            return ResponseEntity.ok(user);
        } catch(UsernameNotFoundException e) {
            System.err.println("Authenticated user not found in DB: " + e.getMessage());
            return ResponseEntity.status(404).build();
        } catch(Exception e) {
            // Catch any unexpected database or service errors
            System.err.println("Error fetching user profile: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }

    @PatchMapping("")
    public ResponseEntity<UserDto> updateProfileProperty(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ProfileUpdateRequest request) { // Updated parameter type

        // This method handles finding the user, updating the property, and validation
        User updatedUser = userService.updateUserProperty(principal.getUsername(), request);
        return ResponseEntity.ok(UserDto.fromEntity(updatedUser));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getProfileStatus(Principal principal) {

        String username = principal.getName();

        User user = userService.getUserByUsername(username);


        boolean complete =
                user.getPersonalName() != null &&
                        user.getPersonalEmail() != null &&
                        user.getPersonalAddress() != null &&
                        user.getProfileText() != null;

        Map<String, Object> response = new HashMap<>();
        response.put("complete", complete);

        return ResponseEntity.ok(response);
    }




}
