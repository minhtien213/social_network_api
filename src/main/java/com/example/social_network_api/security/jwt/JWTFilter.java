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

    private final JWTUtils jwtUtils; // Class tiện ích để xử lý JWT (tạo, parse, validate)
    private final UserDetailsService userDetailsService; // Để load thông tin user từ DB

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

        // 1️⃣ Lấy token từ Header "Authorization"
        final String authHeader = request.getHeader("Authorization");
        // 2️⃣ Nếu header có dạng "Bearer abc.def.ghi" → cắt lấy token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Bỏ chữ "Bearer "
            try {
                // 3️⃣ Dùng JWTUtils để trích xuất username từ token
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                throw new UsernameNotFoundException("Invalid token");
            }
        }
        // 5️⃣ Kiểm tra:
        // - Có username từ token
        // - SecurityContext chưa có Authentication (nghĩa là user chưa đăng nhập)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6️⃣ Load UserDetails từ DB
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7️⃣ Kiểm tra token có hợp lệ với userDetails hay không
            if (jwtUtils.isTokenValid(token, userDetails)) {
                // 8️⃣ Tạo đối tượng Authentication để Spring Security biết user đã xác thực
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,       // Principal
                                null,              // Credentials (để null vì không dùng lại password)
                                userDetails.getAuthorities() // Roles/Authorities
                        );

                // 9️⃣ Gắn thông tin request vào Authentication
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 🔟 Đưa Authentication vào SecurityContext → Spring biết request này đã xác thực
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 1️⃣1️⃣ Cho request đi tiếp qua các filter tiếp theo
        filterChain.doFilter(request, response);
    }
}

