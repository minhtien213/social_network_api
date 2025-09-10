package com.example.social_network_api.controller;

import com.example.social_network_api.dto.respone.TokenResponseDTO;
import com.example.social_network_api.service.GoogleOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/oauth2")
public class GoogleOAuth2Controller {
    private final GoogleOAuth2Service googleOAuth2Service;

    @PostMapping("/google/callback")
    public ResponseEntity<?> handleGoogleLogin(@RequestBody Map<String, String> body)
            throws GeneralSecurityException, IOException {
        String idTokenString = body.get("idToken");
        Map<String, String> response = googleOAuth2Service.handleGoogleLogin(idTokenString);
        return ResponseEntity.ok(new TokenResponseDTO(response.get("accessToken"), response.get("refreshToken")));
    }

}
