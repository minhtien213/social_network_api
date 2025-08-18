package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.AuthRequest;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

        // 1️⃣ Gọi AuthenticationManager để xác thực username & password
        // - UsernamePasswordAuthenticationToken: gói username và password từ request vào object
        // - authenticationManager sẽ tự tìm đúng AuthenticationProvider (ở đây là DaoAuthenticationProvider)
        // - DaoAuthenticationProvider sẽ:
        //   + Gọi UserService.loadUserByUsername() để lấy UserDetails từ DB
        //   + So sánh password từ request (plain text) với password đã mã hóa trong DB (bằng PasswordEncoder)
        // => Nếu đúng: trả về đối tượng Authentication chứa thông tin user
        // => Nếu sai: ném ra BadCredentialsException (Spring Security tự bắt và trả lỗi 401)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // 2️⃣ Nếu AuthenticationManager không ném lỗi → nghĩa là xác thực thành công
        // Lúc này ta load lại thông tin user từ DB (UserDetails) để sinh token
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());

        // 3️⃣ Tạo JWT cho user
        // - jwtUtils.generateToken() sẽ:
        //   + Gắn username vào payload (claims)
        //   + Gắn thời gian hết hạn
        //   + Ký bằng secret key (HS256)
        String token = jwtUtils.generateToken(userDetails);

        // 4️⃣ Trả token về client
        // - Client sẽ lưu token này và gửi lại trong các request tiếp theo (header Authorization: Bearer <token>)
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
            User savedUser = userService.registerUser(userRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserResponseDTO(savedUser));
        }

}
