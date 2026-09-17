package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockNotificationResponse {

    private Integer id;

    private Integer productId;

    private String productName;

    private String category;

    // ★ 配合你目前 Product 的 Double price
    private Double price;

    private Integer stock;

    private String image;

    // 期待價格仍然可以使用 BigDecimal
    private BigDecimal targetPrice;

    private String status;

    private LocalDateTime createdAt;


    public StockNotificationResponse() {
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


    public String getProductName() {
        return productName;
    }

    public void setProductName(
        String productName
    ) {
        this.productName = productName;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(
        String category
    ) {
        this.category = category;
    }


    // ==========================================
    // ★ Double
    // ==========================================

    public Double getPrice() {
        return price;
    }

    public void setPrice(
        Double price
    ) {
        this.price = price;
    }


    public Integer getStock() {
        return stock;
    }

    public void setStock(
        Integer stock
    ) {
        this.stock = stock;
    }


    public String getImage() {
        return image;
    }

    public void setImage(
        String image
    ) {
        this.image = image;
    }


    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(
        BigDecimal targetPrice
    ) {
        this.targetPrice = targetPrice;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(
        String status
    ) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
        LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }
}