package com.example.social_network_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IService<E> {
    void deleteById(Long id);
    Page<E> findAll(int page, int size);
    E findById(Long id);
}

