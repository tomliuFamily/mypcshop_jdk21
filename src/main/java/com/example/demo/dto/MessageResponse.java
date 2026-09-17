package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MessageResponse {


    private Integer id;

    private Integer userId;

    private String userName;

    private String content;

    private LocalDateTime createdAt;


    private List<MessageAttachmentResponse>
        attachments =
            new ArrayList<>();


    private List<MessageReplyResponse>
        replies =
            new ArrayList<>();


    public MessageResponse() {
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(
        LocalDateTime createdAt
    ) {

        this.createdAt = createdAt;
    }


    public List<MessageAttachmentResponse>
        getAttachments() {

        return attachments;
    }


    public void setAttachments(
        List<MessageAttachmentResponse> attachments
    ) {

        this.attachments = attachments;
    }


    public List<MessageReplyResponse>
        getReplies() {

        return replies;
    }


    public void setReplies(
        List<MessageReplyResponse> replies
    ) {

        this.replies = replies;
    }
}