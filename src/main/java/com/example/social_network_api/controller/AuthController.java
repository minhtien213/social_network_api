package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.*;
import com.example.social_network_api.dto.respone.TokenResponseDTO;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.UserMapper;
import com.example.social_network_api.service.RoleService;
import com.example.social_network_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Map<String, String> loginResponse = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new TokenResponseDTO(loginResponse.get("accessToken"), loginResponse.get("refreshToken")));
    }

    // Refresh: chỉ gửi refresh token
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        Map<String, String> refreshTokenResponse = userService.refreshToken(refreshTokenRequestDTO.getRefreshToken());
        return ResponseEntity.ok(new TokenResponseDTO(refreshTokenResponse.get("accessToken"), refreshTokenResponse.get("refreshToken")));
    }

    // Logout: blacklist access + refresh
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        userService.logout(logoutRequestDTO.getAccessToken(), logoutRequestDTO.getRefreshToken());
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
            User savedUser = userService.registerUser(userRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserResponseDTO(savedUser));
    }

    @PostMapping("/reset-request")
    public ResponseEntity<?> resetPassword(@Valid @RequestParam String username) {
        userService.sentPasswordResetToken(username);
        return ResponseEntity.ok("If the user name is correct, " +
                "the email reset the password that has been sent to the registered email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(token, resetPasswordDTO);
        return ResponseEntity.ok().body("Password reset successful.");
    }
}
