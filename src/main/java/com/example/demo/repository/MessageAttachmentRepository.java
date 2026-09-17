package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.MessageAttachment;

public interface MessageAttachmentRepository
        extends JpaRepository<
            MessageAttachment,
            Integer
        > {

    List<MessageAttachment>
        findByMessageIdOrderByIdAsc(
            Integer messageId
        );
}