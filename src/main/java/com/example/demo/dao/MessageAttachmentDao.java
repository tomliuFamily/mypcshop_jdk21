package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.MessageAttachment;

public interface MessageAttachmentDao {

    List<MessageAttachment> saveAll(
        List<MessageAttachment> attachments
    );

    List<MessageAttachment>
        findByMessageId(
            Integer messageId
        );

    Optional<MessageAttachment>
        findById(
            Integer id
        );
}