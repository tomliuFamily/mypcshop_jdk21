package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.MessageReply;


public interface MessageReplyRepository
        extends JpaRepository<MessageReply, Integer> {


    List<MessageReply>
        findByMessageIdOrderByCreatedAtAsc(
            Integer messageId
        );
}