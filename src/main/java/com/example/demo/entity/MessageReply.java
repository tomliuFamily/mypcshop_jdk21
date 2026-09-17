package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "message_replies")
public class MessageReply {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(
        name = "message_id",
        nullable = false
    )
    private Integer messageId;


    @Column(
        name = "sender_user_id",
        nullable = false
    )
    private Integer senderUserId;


    @Column(
        name = "sender_name",
        nullable = false,
        length = 100
    )
    private String senderName;


    @Column(
        name = "sender_role",
        nullable = false,
        length = 20
    )
    private String senderRole;


    @Column(
        name = "content",
        nullable = false,
        length = 1000
    )
    private String content;


    @Column(
        name = "created_at",
        nullable = false
    )
    private LocalDateTime createdAt;


    public MessageReply() {
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