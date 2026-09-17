package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.User;

public interface UserDao {

    Optional<User> findByEmail(
            String email
    );

    Optional<User> findById(
            Integer id
    );

    List<User> findAll();

    User save(
            User user
    );

    boolean existsByEmail(
            String email
    );

    void deleteById(
            Integer id
    );
}