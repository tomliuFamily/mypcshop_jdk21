package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "message_attachments")
public class MessageAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(
        name = "message_id",
        nullable = false
    )
    private Integer messageId;

    @Column(
        name = "original_file_name",
        nullable = false
    )
    private String originalFileName;

    @Column(
        name = "content_type",
        nullable = false
    )
    private String contentType;

    @Column(
        name = "file_size",
        nullable = false
    )
    private Long fileSize;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(
        name = "file_data",
        nullable = false,
        columnDefinition = "LONGBLOB"
    )
    private byte[] fileData;

    @Column(
        name = "upload_time",
        nullable = false
    )
    private LocalDateTime uploadTime;


    public MessageAttachment() {
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


    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(
            String originalFileName) {

        this.originalFileName =
                originalFileName;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(
            String contentType) {

        this.contentType =
                contentType;
    }


    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }


    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }


    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(
            LocalDateTime uploadTime) {

        this.uploadTime =
                uploadTime;
    }
}