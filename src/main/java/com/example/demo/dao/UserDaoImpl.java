package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Repository
public class UserDaoImpl
        implements UserDao {

    @Autowired
    private UserRepository userRepository;


    @Override
    public Optional<User> findByEmail(
            String email) {

        return userRepository
                .findByEmail(email);
    }


    @Override
    public Optional<User> findById(
            Integer id) {

        return userRepository
                .findById(id);
    }


    @Override
    public List<User> findAll() {

        return userRepository
                .findAll();
    }


    @Override
    public User save(User user) {

        return userRepository
                .save(user);
    }


    @Override
    public boolean existsByEmail(
            String email) {

        return userRepository
                .existsByEmail(email);
    }


    @Override
    public void deleteById(
            Integer id) {

        userRepository
                .deleteById(id);
    }
}