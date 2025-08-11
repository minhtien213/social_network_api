package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Post;
import com.example.social_network_api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
