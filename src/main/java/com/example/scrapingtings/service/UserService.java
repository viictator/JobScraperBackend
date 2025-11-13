package com.example.scrapingtings.service;

import com.example.scrapingtings.dto.ProfileUpdateRequest;
import com.example.scrapingtings.model.Role;
import com.example.scrapingtings.model.User;
import com.example.scrapingtings.repository.RoleRepository;
import com.example.scrapingtings.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public User createUser(String username, String rawPassword) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(rawPassword));

        Role defaultRole = roleRepository.findByName("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public User updateUserProperty(String username, ProfileUpdateRequest request) {
        User user = getUserByUsername(username);
        // Access fields using record accessors (request.fieldName() and request.value())
        String fieldName = request.fieldName();
        String newValue = request.value();

        // Handle null values from the DTO
        if (newValue == null) {
            newValue = "";
        }

        switch (fieldName) {
            case "personalName":
                if (!StringUtils.hasText(newValue)) {
                    throw new IllegalArgumentException("Personal name cannot be empty.");
                }
                user.setPersonalName(newValue);
                break;

            case "personalEmail":
                if (!isValidEmail(newValue)) {
                    throw new IllegalArgumentException("Invalid email format.");
                }
                user.setPersonalEmail(newValue);
                break;

            case "personalAddress":
                // Address can be empty
                user.setPersonalAddress(newValue);
                break;

            case "profileText":
                // Profile text allows long input, no strict validation
                user.setProfileText(newValue);
                break;

            default:
                throw new IllegalArgumentException("Unsupported field name for update: " + fieldName);
        }

        // Save the updated user entity
        return userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        if (!StringUtils.hasText(email)) return false;
        // Basic RFC 5322 pattern
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        return pattern.matcher(email).matches();
    }


}
