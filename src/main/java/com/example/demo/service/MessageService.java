package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.MessageReplyResponse;
import com.example.demo.dto.MessageResponse;
import com.example.demo.entity.MessageAttachment;


public interface MessageService {


    List<MessageResponse>
        getAllMessages();


    Page<MessageResponse>
        getMessagePage(
            int page
        );


    MessageResponse
        saveMessageWithFiles(

            String loginEmail,

            String content,

            List<MultipartFile> files

        ) throws IOException;


    MessageAttachment
        getAttachment(
            Integer attachmentId
        );


    MessageReplyResponse
        saveReply(

            Integer messageId,

            String loginEmail,

            String content
        );
}