package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Message;


public interface MessageRepository
        extends JpaRepository<Message, Integer> {


    Page<Message>
        findAllByOrderByCreatedAtDesc(
            Pageable pageable
        );
}