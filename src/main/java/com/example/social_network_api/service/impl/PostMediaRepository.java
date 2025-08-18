package com.example.social_network_api.service.impl;

import com.example.social_network_api.entity.PostMedia;
import org.springframework.data.repository.Repository;

interface PostMediaRepository extends Repository<PostMedia, Long> {
    void deleteAllByPostId(Long postId);
}
