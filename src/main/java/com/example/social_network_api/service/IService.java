package com.example.social_network_api.service;

import java.util.List;

public interface IService<E> {
    void deleteById(Long id);
    List<E> findAll();
    E findById(Long id);
}

