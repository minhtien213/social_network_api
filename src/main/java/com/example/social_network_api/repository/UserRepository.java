package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Role;
import com.example.social_network_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);
    public User findByEmail(String email);

    //check tồn tại cả db
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    //check tồn tại nhưng bỏ qua id đang check
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}
