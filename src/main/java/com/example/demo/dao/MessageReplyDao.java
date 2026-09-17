package com.example.demo.dao;

import java.util.List;

import com.example.demo.entity.MessageReply;


public interface MessageReplyDao {


    MessageReply save(
        MessageReply reply
    );


    List<MessageReply> findByMessageId(
        Integer messageId
    );
}