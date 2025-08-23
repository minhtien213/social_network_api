package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.AuthRequest;
import com.example.social_network_api.dto.request.ResetPasswordDTO;
import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.dto.respone.AuthResponse;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.UserMapper;
import com.example.social_network_api.security.jwt.JWTUtils;
import com.example.social_network_api.service.RoleService;
import com.example.social_network_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try{
            // Gọi AuthenticationManager để xác thực username & password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            // Load lại thông tin user từ DB (UserDetails) để sinh token
            UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
            //  Tạo JWT cho user
            String token = jwtUtils.generateToken(userDetails);
            // Trả token về client
            return ResponseEntity.ok(new AuthResponse(token));
        }catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User is disabled"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
            User savedUser = userService.registerUser(userRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserResponseDTO(savedUser));
    }

    @PostMapping("/reset-request")
    public ResponseEntity<?> resetPassword(@Valid @RequestParam String username) {
        userService.sentPasswordResetToken(username);
        return ResponseEntity.ok("If the user name is correct, the email reset the password that has been sent to the registered email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(token, resetPasswordDTO);
        return ResponseEntity.ok().body("Password reset successful.");
    }
}
