package com.example.social_network_api.security;

import com.example.social_network_api.security.jwt.JWTFilter;
import com.example.social_network_api.security.ratelimiting.RateLimitFilter;
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

    private final JWTFilter jwtFilter;
    private final UserService userService;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfiguration(JWTFilter jwtFilter, @Lazy UserService userService, RateLimitFilter rateLimitFilter) {
        this.jwtFilter = jwtFilter;
        this.userService = userService;
        this.rateLimitFilter = rateLimitFilter;
    }

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // bật CORS mặc định
                .csrf(csrf -> csrf.disable()) // ignore CSRF cho WS handshake
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/ws/**").permitAll() // cho phép Websocket
                        .requestMatchers(
                                "/chat.html",   // file test chat massage
                                "/notification.html",    // file test notification
                                "/oauth2_google.html",    // file test goole login
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/post/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/profile/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/user/list-users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/profile/list-profiles").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/post/list-posts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/comment/list-comments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/like/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/follows/*/friends").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/follows/*/followers").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/follows/*/followings").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/follows/*/friend-counts").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/notifications/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/notifications/*/notification-counts").hasAnyRole("USER", "ADMIN")


                        .requestMatchers(HttpMethod.POST, "/api/post/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/profile/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/comment/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/follows/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/follows/*/accept").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/follows/*/reject").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/user/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/post/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/profile/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/comment/*").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/user/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/post/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/comment/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/follows/*/cancel").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/follows/*/unfollow").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/profile/*").hasRole("ADMIN")

                        .anyRequest().authenticated() // Các request khác phải có token hợp lệ
                )
                .authenticationProvider(authenticationProvider()) // Provider dùng DB + BCrypt
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // thêm filter JWT trước filter mặc định
                .addFilterAfter(rateLimitFilter, JWTFilter.class); // thêm RateLimitFilter chạy sau JWTFilter

        return http.build();
    }
}

