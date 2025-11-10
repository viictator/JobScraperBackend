package com.example.scrapingtings.config;

import com.example.scrapingtings.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless/API usage
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/api/public/**").permitAll()
                        // ADMIN access only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // USER or ADMIN access
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService) // Use your custom service
                .httpBasic(Customizer.withDefaults()); // Enable Basic Auth (or formLogin/JWT)

        return http.build();
    }

}
