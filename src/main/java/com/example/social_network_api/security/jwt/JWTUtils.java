package com.example.social_network_api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15;  // 15 phút
    private final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 7 ngày

    public String generateToken(Map<String, Object> claims, String username, long validity) {
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setId(jti)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type_token", "access_token");
        return generateToken(claims, username, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type_token", "refresh_token");
        return generateToken(claims, username, REFRESH_TOKEN_VALIDITY);
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    public String extractJti(String token) {
        return parseToken(token).getId();
    }

    public Date extractExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    public String extractTokenType(String token) {
        return parseToken(token).get("type_token").toString();
    }

    public long secondsUntilExpiry(String token) {
        long diffMs = extractExpiration(token).getTime() - System.currentTimeMillis();
        return Math.max(0, diffMs / 1000);
    }

    // Kiểm tra token đã hết hạn chưa
    private boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getExpiration();
        return expiration.before(new Date());
    }

    // Kiểm tra token có hợp lệ không
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}

