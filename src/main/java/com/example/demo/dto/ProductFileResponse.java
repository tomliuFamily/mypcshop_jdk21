package com.example.demo.dto;

import java.time.LocalDateTime;

public class ProductFileResponse {

    private Integer id;

    private Integer productId;

    private String originalFileName;

    private String contentType;

    private Long fileSize;

    private LocalDateTime uploadTime;


    public ProductFileResponse() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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


    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(
            LocalDateTime uploadTime) {

        this.uploadTime = uploadTime;
    }
}