package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.example.demo.entity.Message;


public interface MessageDao {


    List<Message> findAll();


    Message save(
        Message message
    );


    Page<Message> findPage(
        int page,
        int size
    );


    Optional<Message> findById(
        Integer id
    );
}