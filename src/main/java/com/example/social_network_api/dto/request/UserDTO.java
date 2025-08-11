package com.example.social_network_api.dto.request;

import com.example.social_network_api.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

//tạo các validation khi register user
public class UserDTO {

    @NotBlank(message = "Khong duoc de trong!")
    private String username;
    @NotBlank(message = "Do dai toi thieu 8 ki tu!")
    @Size(min = 4, max = 8)
//    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$")
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email(message = "Email khong hop le!")
    private String email;
    private List<Role> roles;

    public UserDTO() {
    }

    public UserDTO(String username, String password, String firstName, String lastName, String email, List<Role> roles) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
