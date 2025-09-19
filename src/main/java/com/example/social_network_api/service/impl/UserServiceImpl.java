package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.request.ResetPasswordDTO;
import com.example.social_network_api.dto.request.UpdateUserRequestDTO;
import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.entity.PasswordResetToken;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.BadRequestException;
import com.example.social_network_api.exception.custom.ConflictException;
import com.example.social_network_api.exception.custom.ForbiddenException;
import com.example.social_network_api.exception.custom.ResourceNotFoundException;
import com.example.social_network_api.repository.PasswordResetTokenRepositoty;
import com.example.social_network_api.repository.ProfileRepository;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.security.jwt.JWTUtils;
import com.example.social_network_api.service.*;
import com.example.social_network_api.utils.AuthUtils;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final TokenStoreService tokenStoreService;
    private final PasswordResetTokenRepositoty passwordResetTokenRepositoty;
    private final RoleService roleService;
    private final MailService mailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }

    // ROLE_USER → new SimpleGrantedAuthority("ROLE_USER")
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> {
                    return new SimpleGrantedAuthority(role.getName());
                })
                .collect(Collectors.toList());
    }

    public Map<String, String> login(String username, String password) {
        // Gọi AuthenticationManager để xác thực username & password
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Load lại thông tin user từ DB (UserDetails) để sinh token
        UserDetails userDetails = this.loadUserByUsername(username);

        String accessToken = jwtUtils.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

        // Lưu refresh jti vào whitelist (ttl: time to live)
        String refreshJti = jwtUtils.extractJti(refreshToken);
        long refreshTtlSec = jwtUtils.secondsUntilExpiry(refreshToken);
        tokenStoreService.whitelistRefreshJti(refreshJti, username, refreshTtlSec);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        // 1) Parse & kiểm tra loại
        Claims claims = jwtUtils.parseToken(refreshToken);
        String tokenType = (String) claims.get("token_type");
        if (!"refresh".equals(tokenType)) {
            throw new BadRequestException("Invalid token_type for refresh flow");
        }

        String jti = claims.getId();
        String username = claims.getSubject();

        // 2) Check blacklist
        if (tokenStoreService.isRefreshJtiBlacklisted(jti)) {
            throw new RuntimeException("Refresh token is blacklisted (reused or revoked)");
        }

        // 3) Check whitelist tồn tại (single-use/single-session)
        String wlUsername = tokenStoreService.getUsernameByRefreshJti(jti);
        if (wlUsername == null || !wlUsername.equals(username)) {
            throw new RuntimeException("Refresh token not recognized (revoked or expired)");
        }

        // 4) (Optional) load lại user để đảm bảo user còn active/roles mới nhất
        UserDetails userDetails = this.loadUserByUsername(username);
        //List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        // 5) ROTATE: blacklist refresh cũ + xoá whitelist cũ + phát refresh mới + whitelist mới
        long oldRtTtl = jwtUtils.secondsUntilExpiry(refreshToken);
        tokenStoreService.blacklistRefreshJti(jti, oldRtTtl); // chống reuse
        tokenStoreService.removeRefreshJti(jti);

        String newAccess = jwtUtils.generateAccessToken(username);
        String newRefresh = jwtUtils.generateRefreshToken(username);

        //whitelist new refresh token
        String newJti = jwtUtils.extractJti(newRefresh);
        long newRtTtl = jwtUtils.secondsUntilExpiry(newRefresh);
        tokenStoreService.whitelistRefreshJti(newJti, username, newRtTtl);

        return Map.of(
                "accessToken", newAccess,
                "refreshToken", newRefresh
        );
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        // blacklist access token
        if (accessToken != null && !accessToken.isBlank()) {
            String atJti = jwtUtils.extractJti(accessToken);
            long ttl = jwtUtils.secondsUntilExpiry(accessToken);
            if (ttl > 0) tokenStoreService.blacklistAccessJti(atJti, ttl);
        }

        // blacklist refresh token
        if (refreshToken != null && !refreshToken.isBlank()) {
            String rtJti = jwtUtils.extractJti(refreshToken);
            long ttl = jwtUtils.secondsUntilExpiry(refreshToken);
            if (ttl > 0) {
                tokenStoreService.blacklistRefreshJti(rtJti, ttl);
                tokenStoreService.removeRefreshJti(rtJti); // xoá khỏi whitelist
            }
        }
    }

    @Transactional
    public User registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
            throw new ConflictException("Username already exists!");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new ConflictException("Email already exists!");
        }

        if (userRequestDTO.getPassword().length() < 4) {
            throw new BadRequestException("Password too short!");
        }

        User user = User.builder()
                .username(userRequestDTO.getUsername())
                .enabled(true)
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .email(userRequestDTO.getEmail())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .build();

        Role defaultRole = roleService.findByName("ROLE_USER");
        user.setRoles(defaultRole != null ? List.of(defaultRole) : null);

        Profile profile = new Profile();
        profile.setUser(user);
        user.setProfile(profile);

        return userRepository.save(user);
    }

    @Override
    public void sentPasswordResetToken(String username) {
        User user = this.findByUsername(username);
        if (user != null && user.isEnabled()) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusMinutes(15))
                    .build();
            passwordResetTokenRepositoty.save(passwordResetToken);

            String resetLink = "Truy cập link sau trong vòng 15 phút để đặt lại mật khẩu của bạn: " +
                    "http://localhost:8080/reset-password?token=" + token;
            mailService.sendMail(user.getEmail(), "Resset Password - Test APP", resetLink);
        }
    }

    @Override
    public void resetPassword(String token, ResetPasswordDTO resetPasswordDTO) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepositoty.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found or Token has been used!"));
        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token is expired!");
        }
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getNewPasswordConfirm())) {
            throw new BadRequestException("New Password Mismatch!");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        userRepository.save(user);

        // xóa token sau khi dùng
        passwordResetTokenRepositoty.delete(passwordResetToken);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UpdateUserRequestDTO updateUserRequestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!AuthUtils.getCurrentUsername().equals(existingUser.getUsername())
                && !AuthUtils.isAdmin()) {
            throw new ForbiddenException("Unauthorized");
        }

        //check các id còn lại trong db xem có id nào trùng username muốn update không
        if (userRepository.existsByUsernameAndIdNot(updateUserRequestDTO.getUsername(), id)) {
            throw new ConflictException("Username already exists!");
        }

        if (userRepository.existsByEmailAndIdNot(updateUserRequestDTO.getEmail(), id)) {
            throw new ConflictException("Email already exists!");
        }

        if(updateUserRequestDTO.getUsername() != null){
            existingUser.setUsername(updateUserRequestDTO.getUsername());
        }
        if(updateUserRequestDTO.getEmail() != null){
            existingUser.setEmail(updateUserRequestDTO.getEmail());
        }
        if(updateUserRequestDTO.getFirstName() != null){
            existingUser.setFirstName(updateUserRequestDTO.getFirstName());
        }
        if(updateUserRequestDTO.getLastName() != null){
            existingUser.setLastName(updateUserRequestDTO.getLastName());
        }
        if (updateUserRequestDTO.getPassword() != null && !updateUserRequestDTO.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updateUserRequestDTO.getPassword()));
        }
        existingUser.setUpdatedAt(LocalDateTime.now());

        // Chỉ set các field != null
        // userMapper.updateUserFromDto(userRequestDTO, existingUser);

        return userRepository.save(existingUser);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public Page<User> findByFullName(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findByFullName(keyword, pageable);
        return users;
    }

    @Override
    public Page<User> findAll(int page, int size) {
        if (!AuthUtils.isAdmin()) {
            throw new ForbiddenException("Unauthorized");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!AuthUtils.isAdmin()){
            throw new ForbiddenException("Unauthorized.");
        }
        profileRepository.deleteByUserId(user.getId());
        userRepository.deleteById(user.getId());
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!AuthUtils.getCurrentUsername().equals(user.getUsername()) && !AuthUtils.isAdmin()){
            throw new ForbiddenException("Unauthorized.");
        }
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!AuthUtils.getCurrentUsername().equals(user.getUsername()) && !AuthUtils.isAdmin()){
            throw new ForbiddenException("Unauthorized.");
        }
        user.setEnabled(true);
        userRepository.save(user);
    }
}
