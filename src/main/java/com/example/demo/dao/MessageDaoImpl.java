package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Message;
import com.example.demo.repository.MessageRepository;


@Repository
public class MessageDaoImpl
        implements MessageDao {


    private final MessageRepository
        messageRepository;


    public MessageDaoImpl(
        MessageRepository messageRepository
    ) {

        this.messageRepository =
            messageRepository;
    }


    @Override
    public List<Message> findAll() {

        return messageRepository.findAll();
    }


    @Override
    public Message save(
        Message message
    ) {

        return messageRepository.save(
            message
        );
    }


    @Override
    public Page<Message> findPage(
        int page,
        int size
    ) {


        Pageable pageable =
            PageRequest.of(
                page,
                size
            );


        return messageRepository
            .findAllByOrderByCreatedAtDesc(
                pageable
            );
    }


    @Override
    public Optional<Message> findById(
        Integer id
    ) {

        return messageRepository.findById(
            id
        );
    }
}