package com.example.social_network_api.service.impl;

import com.example.social_network_api.dto.request.UserDTO;
import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.UserMapper;
import com.example.social_network_api.repository.UserRepository;
import com.example.social_network_api.service.RoleService;
import com.example.social_network_api.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // Hàm loadUserByUsername
// Mục đích: Spring Security sẽ gọi hàm này khi cần xác thực username/password.
// - DaoAuthenticationProvider sẽ gọi nó bên trong quá trình authenticate().
// - Trả về một UserDetails để Spring so sánh mật khẩu và gán quyền (roles).
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ghi log để theo dõi xem username nào đang được yêu cầu load
        logger.info("Attempting to load user with username={}", username);

        // 1️⃣ Tìm user trong DB theo username
        User user = userRepository.findByUsername(username);

        // 2️⃣ Nếu không tìm thấy → log cảnh báo và ném lỗi
        if (user == null) {
            logger.warn("User not found in database: username={}", username);
            throw new UsernameNotFoundException("User not found");
        }

        if(!user.isEnabled()){
            throw new DisabledException("User is disabled");
        }

        // 3️⃣ Nếu tìm thấy → log thông tin user (chỉ log roles, không log password)
        logger.info("User found: username={}, roles={}", user.getUsername(), user.getRoles());

        // 4️⃣ Trả về UserDetails của Spring Security
        // - user.getPassword(): password đã mã hoá trong DB
        // - mapRolesToAuthorities(): chuyển từ danh sách Role sang GrantedAuthority
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // ⚠ Không log password ra console
                mapRolesToAuthorities(user.getRoles())
        );
    }


    // Hàm mapRolesToAuthorities
// Mục đích: chuyển danh sách Role trong DB sang danh sách GrantedAuthority mà Spring Security hiểu.
// Ví dụ: ROLE_USER → new SimpleGrantedAuthority("ROLE_USER")
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        // Ghi log các roles đang được mapping
        logger.debug("Mapping roles to authorities: {}", roles);

        // 1️⃣ Duyệt từng Role và chuyển sang SimpleGrantedAuthority
        // 2️⃣ Collect thành danh sách List<GrantedAuthority>
        return roles.stream()
                .map(role -> {
                    logger.debug("Mapping role={} to GrantedAuthority", role.getName());
                    return new SimpleGrantedAuthority(role.getName());
                })
                .collect(Collectors.toList());
    }


    // Đăng ký user mới (nếu bạn có chức năng register)
    @Override
    @Transactional
    public User save(User user) {
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("Username already exists!");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        Role defaultRole = roleService.findByName("ROLE_USER");
        user.setRoles(List.of(defaultRole));

        return  userRepository.save(user);
    }

    // Tìm user theo username
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new UsernameNotFoundException("Users not found");
        }
        return users;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.deleteById(user.getId());
    }

    @Override
    public void disableUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void enableUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userRepository.existsByUsernameAndIdNot(user.getUsername(), id)) {
            throw new RuntimeException("Username already exists!");
        }

        if (userRepository.existsByEmailAndIdNot(user.getEmail(), id)) {
            throw new RuntimeException("Email already exists!");
        }

        // Copy field từ `user` sang `existingUser` nhưng bỏ qua các field nhạy cảm
        BeanUtils.copyProperties(
                user,
                existingUser,
                "id", "password", "roles", "enabled", "createdAt"
        );

        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(existingUser);
    }

}
