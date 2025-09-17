package com.example.social_network_api.repository;

import com.example.social_network_api.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);

    @Query("select p from Profile p where LOWER(p.fullName) like LOWER(CONCAT('%', :keyword, '%'))")
    Page<Profile> findByFullName(@Param("keyword") String keyword, Pageable pageable);
}

