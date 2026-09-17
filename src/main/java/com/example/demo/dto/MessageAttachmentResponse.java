package com.example.demo.dto;

public class MessageAttachmentResponse {

    private Integer id;

    private String originalFileName;

    private String contentType;

    private Long fileSize;


    public MessageAttachmentResponse() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}