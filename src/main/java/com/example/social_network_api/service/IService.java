package com.example.social_network_api.service;

import java.util.List;

public interface IService<E> {
    E save(E e);
    E update(Long id, E e);
    void deleteById(Long id);
    List<E> findAll();
    E findById(Long id);
}

