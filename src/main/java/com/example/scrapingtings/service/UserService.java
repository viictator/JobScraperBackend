package com.example.scrapingtings.service;

import com.example.scrapingtings.model.Role;
import com.example.scrapingtings.model.User;
import com.example.scrapingtings.repository.RoleRepository;
import com.example.scrapingtings.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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


}
