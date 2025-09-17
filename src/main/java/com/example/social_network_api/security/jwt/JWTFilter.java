package com.example.social_network_api.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    // @Lazy giúp tránh vòng lặp phụ thuộc (bean cycle)
    public JWTFilter(JWTUtils jwtUtils, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    // doFilterInternal: chạy cho MỖI request (ngoại trừ những request bị bỏ qua trong SecurityConfig)
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String username = null;
        String token = null;

        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Bỏ chữ "Bearer "
            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                throw new UsernameNotFoundException("Invalid token");
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtils.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,       // Principal
                                null,              // Credentials (để null vì không dùng lại password)
                                userDetails.getAuthorities() // Roles/Authorities
                        );

                // Gắn thông tin request vào Authentication
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đưa Authentication vào SecurityContext → Spring biết request này đã xác thực
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Cho request đi tiếp qua các filter tiếp theo
        filterChain.doFilter(request, response);
    }
}

