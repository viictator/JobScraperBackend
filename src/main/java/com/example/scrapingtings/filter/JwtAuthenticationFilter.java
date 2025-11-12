package com.example.scrapingtings.filter;

import com.example.scrapingtings.service.CustomUserDetailsService;
import com.example.scrapingtings.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException; // Import to handle expiration explicitly
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Check for token presence and format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract JWT (starting at index 7, after "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 3. Extract Username from token (requires signature validation)
            username = jwtService.extractUsername(jwt);

        } catch (ExpiredJwtException e) {
            // Handle expired token explicitly to log it
            System.err.println("Token expired for user: " + e.getClaims().getSubject());
            // Let the filter continue; the authentication entry point will handle the 401
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            // Handle other general validation errors (e.g., malformed token)
            System.err.println("JWT processing error: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }


        // 4. Check if user is extracted and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Load UserDetails (Triggers your Hibernate queries)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Validate the token against the loaded UserDetails
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. If valid, update SecurityContext (authentication successful!)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // Authorities MUST contain 'ROLE_' prefix (e.g., ROLE_USER)
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("User authenticated successfully: " + username);
            } else {
                // This path is hit if the token is validly signed but expired (the current issue)
                System.err.println("Token validation failed for user: " + username + " (Likely expired)");
            }
        }

        filterChain.doFilter(request, response);
    }
}