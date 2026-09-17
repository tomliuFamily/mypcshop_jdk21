package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.MessageAttachment;
import com.example.demo.repository.MessageAttachmentRepository;

@Repository
public class MessageAttachmentDaoImpl
        implements MessageAttachmentDao {


    private final
        MessageAttachmentRepository
            messageAttachmentRepository;


    public MessageAttachmentDaoImpl(
        MessageAttachmentRepository
            messageAttachmentRepository
    ) {

        this.messageAttachmentRepository =
            messageAttachmentRepository;
    }


    @Override
    public List<MessageAttachment>
            saveAll(
                List<MessageAttachment>
                    attachments
            ) {

        return messageAttachmentRepository
            .saveAll(
                attachments
            );
    }


    @Override
    public List<MessageAttachment>
            findByMessageId(
                Integer messageId
            ) {

        return messageAttachmentRepository
            .findByMessageIdOrderByIdAsc(
                messageId
            );
    }


    @Override
    public Optional<MessageAttachment>
            findById(
                Integer id
            ) {

        return messageAttachmentRepository
            .findById(
                id
            );
    }
}