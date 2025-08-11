package com.example.social_network_api.security;

import com.example.social_network_api.security.jwt.JWTFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.social_network_api.service.UserService;

@Configuration
public class SecurityConfiguration {

    // Filter để kiểm tra và xác thực JWT token trong mỗi request
    private final JWTFilter jwtFilter;

    // Service để load thông tin user từ database
    private final UserService userService;

    // Constructor injection, @Lazy để tránh vòng lặp bean
    public SecurityConfiguration(JWTFilter jwtFilter, @Lazy UserService userService) {
        this.jwtFilter = jwtFilter;
        this.userService = userService;
    }

    // Bean mã hóa password theo chuẩn BCrypt (bảo mật cao)
    // Dùng để so sánh password khi user login
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cung cấp AuthenticationManager để xử lý xác thực đăng nhập
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Xác thực user khi gọi authenticationManager.authenticate(...) ở AuthContoller
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // Lấy user từ DB
        authProvider.setPasswordEncoder(passwordEncoder()); // Mã hóa/so sánh password
        return authProvider;
    }

    /**
     * - Cấu hình chuỗi filter bảo mật
     * - Tắt CSRF (vì dùng JWT, không cần CSRF token)
     * - Chế độ Stateless không lưu session vì sẽ check token mỗi lần request)
     * - Thêm jwtFilter trước filter UsernamePasswordAuthenticationFilter để check JWT
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Tắt CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() // Cho phép login không cần token
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/list-users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/user/{id}").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated() // Các request khác phải có token hợp lệ
                )
                .authenticationProvider(authenticationProvider()) // Provider dùng DB + BCrypt
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Thêm filter JWT trước filter mặc định

        return http.build();
    }
}

