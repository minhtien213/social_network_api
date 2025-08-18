package com.example.social_network_api.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtils {

    // Đọc giá trị secret key từ file application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Sinh JWT token dựa trên thông tin UserDetails
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // setSubject: gắn username vào phần payload của token
                .setIssuedAt(new Date()) // setIssuedAt: thời điểm token được tạo
                // setExpiration: thời điểm token hết hạn (ở đây là 10 giờ sau khi tạo)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                // signWith: ký token bằng thuật toán HS256 và secret key
                // LƯU Ý: jwtSecret phải đủ dài >= 256 bit nếu dùng HS256
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact(); // compact(): hoàn thiện và trả token dưới dạng String
    }

    // Trích xuất username từ token
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret) // setSigningKey: cung cấp key để giải mã token
                .parseClaimsJws(token) // parseClaimsJws: giải mã token và xác thực chữ ký
                .getBody() // getBody: lấy phần payload của token
                .getSubject(); // getSubject: lấy ra giá trị username
    }

    // Kiểm tra token đã hết hạn chưa
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());  // Nếu thời gian hết hạn trước thời điểm hiện tại => đã hết hạn
    }

    // Kiểm tra token có hợp lệ không
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        // Token hợp lệ nếu username khớp và chưa hết hạn
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}

