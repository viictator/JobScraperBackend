package com.example.scrapingtings.config;

import com.example.scrapingtings.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 1. CORS Configuration: ESSENTIAL for cross-origin cookie/session exchange
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Set the allowed origin to your Next.js frontend URL
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Allow necessary methods and headers
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        // CRITICAL: Allows cookies (like JSESSIONID) to be sent and received
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Integrate the CORS configuration
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API usage
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/api/public/**").permitAll()
                        // Allow POST to /logout 
                        .requestMatchers(HttpMethod.POST, "/logout").permitAll()
                        // ADMIN access only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // USER or ADMIN access
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())

                // 2. EXPLICIT LOGOUT CONFIGURATION
                .logout(logout -> logout
                        .logoutUrl("/logout")               // Defines the URL to trigger logout (POST /logout)
                        .invalidateHttpSession(true)        // Invalidates the server-side session
                        .deleteCookies("JSESSIONID")        // Instructs the browser to delete the cookie
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Returns a 200 OK status for the API client
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                );

        return http.build();
    }

}