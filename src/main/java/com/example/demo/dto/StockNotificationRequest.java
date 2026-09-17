package com.example.demo.dto;

import java.math.BigDecimal;

public class StockNotificationRequest {

    private Integer userId;

    private Integer productId;

    private BigDecimal targetPrice;


    public StockNotificationRequest() {
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(
        BigDecimal targetPrice
    ) {
        this.targetPrice = targetPrice;
    }
}