package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.UserDTO;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.UserMapper;
import com.example.social_network_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserDTO(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: User not found");
        }
    }

    @GetMapping("/list-users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            List<UserDTO> userResponses = users.stream().map(user -> userMapper.toUserDTO(user)).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(userResponses);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        try {
            userService.disableUser(id);
            return ResponseEntity.ok("User has been disabled");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error while disabling user");
        }
    }

    @PutMapping("/enable/{id}")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        try {
            userService.enableUser(id);
            return ResponseEntity.ok("User has been enabled");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error while enabling user");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("User has been deleted");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Error while deleting user");
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UserDTO userDTO) {
        try {
            User updatedUser = userService.update(id, userMapper.toUser(userDTO));
            return ResponseEntity.ok(userMapper.toUserDTO(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
