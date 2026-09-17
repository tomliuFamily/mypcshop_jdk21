package com.example.demo.service;

import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;

public interface UserService {

    LoginResponse login(
            String email,
            String password
    );

    User createUser(
            User user
    );
}