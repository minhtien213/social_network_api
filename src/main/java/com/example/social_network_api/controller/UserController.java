package com.example.social_network_api.controller;

import com.example.social_network_api.dto.request.UpdateUserRequestDTO;
import com.example.social_network_api.dto.request.UserRequestDTO;
import com.example.social_network_api.dto.respone.UserResponseDTO;
import com.example.social_network_api.entity.User;
import com.example.social_network_api.mapper.UserMapper;
import com.example.social_network_api.service.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userMapper.toUserResponseDTO(user));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<?>> findByFullName(@RequestParam String keyword,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "3") int size) {
        Page<User> users = userService.findByFullName(keyword, page, size);
        Page<UserResponseDTO> dtos = users.map(userMapper::toUserResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    @GetMapping("/list-users")
    public ResponseEntity<Page<?>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "3") int size) {
        Page<User> users = userService.findAll(page, size);
        Page<UserResponseDTO> usersResponse = users.map(userMapper::toUserResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(usersResponse);
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok("User has been disabled");
    }

    @PutMapping("/enable/{id}")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok("User has been enabled");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User has been deleted");
    }

    @GetMapping("/display-fullname")
    public ResponseEntity<Page<?>> findByFirstNameAndLastName(@RequestParam("keyword") String keyword,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size){
        Page<User> users = userService.findByFirstNameAndLastName(keyword, page, size);
        Page<UserResponseDTO> dtos = users.map(userMapper::toUserResponseDTO);
        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
            User updatedUser = userService.updateUser(id, updateUserRequestDTO);
            return ResponseEntity.ok(userMapper.toUserResponseDTO(updatedUser));
    }

}
