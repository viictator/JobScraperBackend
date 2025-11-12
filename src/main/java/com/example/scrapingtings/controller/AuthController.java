package com.example.scrapingtings.controller;

import com.example.scrapingtings.service.JwtService;
import com.example.scrapingtings.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// Allow the Next.js frontend to send login credentials
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Data Transfer Object (DTO) for login request body
    // You should typically define this as a separate class, but we'll use a record
    // or inner class here for simplicity if you don't have a separate DTO package.
    record LoginRequest(String username, String password) {}

    // Response object structure for the token
    record AuthResponse(String token) {}

    /**
     * Handles POST requests for user login.
     * Takes username and password, authenticates, and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Authenticate the user credentials
            // This calls the DaoAuthenticationProvider configured in SecurityConfig,
            // which uses CustomUserDetailsService and PasswordEncoder.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // If authentication is successful, Spring returns a fully populated Authentication object.
            // 2. Load the full UserDetails object
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());

            // 3. Generate the JWT token
            final String jwt = jwtService.generateToken(userDetails);

            // 4. Return the token to the client
            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (Exception e) {
            // Catches exceptions like BadCredentialsException
            System.err.println("Authentication failed for user: " + loginRequest.username() + ". Error: " + e.getMessage());
            // Return 401 Unauthorized for failed login
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}