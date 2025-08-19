package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.entity.Profile;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.exception.custom.BadRequestException;
import com.example.social_network_api.exception.custom.ConflictException;
import com.example.social_network_api.exception.custom.ResourceNotfoundException;
import com.example.social_network_api.mapper.UserMapper;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.service.RoleService;
import com.example.social_network_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;

    // Hàm loadUserByUsername
    // Mục đích: Spring Security sẽ gọi hàm này khi cần xác thực username/password.
    // - DaoAuthenticationProvider sẽ gọi nó bên trong quá trình authenticate().
    // - Trả về một UserDetails để Spring so sánh mật khẩu và gán quyền (roles).
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1️⃣ Tìm user trong DB theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)
                );

        if(!user.isEnabled()){
            throw new DisabledException("User is disabled");
        }

        // 4️⃣ Trả về UserDetails của Spring Security
        // - user.getPassword(): password đã mã hoá trong DB
        // - mapRolesToAuthorities(): chuyển từ danh sách Role sang GrantedAuthority
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }


    // Hàm mapRolesToAuthorities
    // Mục đích: chuyển danh sách Role trong DB sang danh sách GrantedAuthority mà Spring Security hiểu.
    // Ví dụ: ROLE_USER → new SimpleGrantedAuthority("ROLE_USER")
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {

        // 1️⃣ Duyệt từng Role và chuyển sang SimpleGrantedAuthority
        // 2️⃣ Collect thành danh sách List<GrantedAuthority>
        return roles.stream()
                .map(role -> {
                    return new SimpleGrantedAuthority(role.getName());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public User registerUser(UserRequestDTO userRequestDTO) {
        if(userRepository.existsByUsername(userRequestDTO.getUsername())){
            throw new ConflictException("Username already exists!");
        }
        if(userRepository.existsByEmail(userRequestDTO.getEmail())){
            throw new ConflictException("Email already exists!");
        }

        if(userRequestDTO.getPassword().length() < 4){
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

        return  userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));

        if (userRepository.existsByUsernameAndIdNot(userRequestDTO.getUsername(), id)) {
            throw new ConflictException("Username already exists!");
        }

        if (userRepository.existsByEmailAndIdNot(userRequestDTO.getEmail(), id)) {
            throw new ConflictException("Email already exists!");
        }

        // Copy field từ user sang existingUser nhưng bỏ qua các field nhạy cảm
        BeanUtils.copyProperties(
                userRequestDTO,
                existingUser,
                "id", "password", "roles", "enabled", "createdAt"
        );
        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));
    }

    // Tìm user theo username
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new ResourceNotfoundException("Users not found");
        }
        return users;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));
        userRepository.deleteById(user.getId());
    }

    @Override
    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
