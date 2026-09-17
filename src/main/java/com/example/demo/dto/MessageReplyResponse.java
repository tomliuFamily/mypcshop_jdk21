package com.example.demo.dto;

import java.time.LocalDateTime;


public class MessageReplyResponse {

    private Integer id;

    private Integer messageId;

    private Integer senderUserId;

    private String senderName;

    private String senderRole;

    private String content;

    private LocalDateTime createdAt;


    public MessageReplyResponse() {
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getMessageId() {
        return messageId;
    }


    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }


    // ==========================================
    // ★ 這兩個就是你目前缺少的
    // ==========================================

    public Integer getSenderUserId() {
        return senderUserId;
    }


    public void setSenderUserId(Integer senderUserId) {
        this.senderUserId = senderUserId;
    }


    public String getSenderName() {
        return senderName;
    }


    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public String getSenderRole() {
        return senderRole;
    }


    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
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


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}