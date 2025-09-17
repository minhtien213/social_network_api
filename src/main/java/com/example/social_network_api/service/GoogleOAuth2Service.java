package com.example.social_network_api.service;

import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.repository.RoleRepository;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.security.jwt.JWTUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

//    @Value("${google.client.id}")
    String googleClientId = "711156209427-i9t1kcc0705ggtifi24j477k3pvc5e11.apps.googleusercontent.com";

    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public Map<String, String> handleGoogleLogin(String idTokenString)
            throws GeneralSecurityException, IOException {

        String accessToken;
        String refreshToken;

        // Tạo verifier để verify ID token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),     // HTTP client -> để tải public key từ Google
                new JacksonFactory()        // JSON parser -> để parse response từ Google
        )
                .setAudience(
//                        Collections.singletonList(googleClientId)
                        Arrays.asList(
                                googleClientId,
                                "407408718192.apps.googleusercontent.com" // OAuth Playground client_id
                        )
                ) // check token có cấp cho app mình không
                .build();

        // Verify token từ idTokenString client
        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken == null) {
            throw new IllegalArgumentException("Invalid Google ID token.");
        }

        // Lấy payload (claims)
        GoogleIdToken.Payload payload = idToken.getPayload();

        // Lấy claims
        String email = payload.getEmail();

        // Check DB user theo email
        User user;
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            user = existingUser.get(); //gán vào user nếu đã tồn tại
        } else {
            // tạo mới user nếu chưa tồn tại
            Role defaultRole = roleRepository.findByName("ROLE_USER");
            User newUser = User.builder()
                    .username(UUID.randomUUID().toString())
                    .email(email)
                    .enabled(true)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .roles(defaultRole != null ? List.of(defaultRole) : null)
                    .build();

            Profile profile = new Profile();
            profile.setUser(newUser);
            newUser.setProfile(profile);

            user = userRepository.save(newUser);
        }

        accessToken = jwtUtils.generateAccessToken(user.getUsername());
        refreshToken = jwtUtils.generateRefreshToken(user.getUsername());
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }
}
