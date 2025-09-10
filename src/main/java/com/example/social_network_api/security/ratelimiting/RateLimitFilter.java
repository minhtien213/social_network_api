package com.example.social_network_api.security.ratelimiting;

import com.example.social_network_api.utils.AuthUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // lấy auth từ context khi có request
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken) ? auth.getName() : null;

        // nếu chưa login -> set bằng ip
        String key = (username != null)
                ? "rate_limit:user" + username : "rate_limit:ip" + request.getRemoteAddr();

        // increment(key) - vừa set key vừa tăng count
        Long count = redisTemplate.opsForValue().increment(key);

        // nếu request đầu thì set expire 60s
        if(count == 1){
            redisTemplate.expire(key, Duration.ofSeconds(60));
        }

        // giới hạn 5 request / phút
        if(count != null && count > 5){
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests, try again later.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
