package com.example.demo.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.MessageReply;
import com.example.demo.repository.MessageReplyRepository;


@Repository
public class MessageReplyDaoImpl
        implements MessageReplyDao {


    private final MessageReplyRepository
        messageReplyRepository;


    public MessageReplyDaoImpl(
        MessageReplyRepository messageReplyRepository
    ) {

        this.messageReplyRepository =
            messageReplyRepository;
    }


    @Override
    public MessageReply save(
        MessageReply reply
    ) {

        return messageReplyRepository.save(
            reply
        );
    }


    @Override
    public List<MessageReply> findByMessageId(
        Integer messageId
    ) {

        return messageReplyRepository
            .findByMessageIdOrderByCreatedAtAsc(
                messageId
            );
    }
}