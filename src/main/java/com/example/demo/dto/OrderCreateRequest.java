package com.example.demo.dto;

import java.util.List;

public class OrderCreateRequest {

    private Integer userId;

    private List<OrderItemRequest> items;

    public OrderCreateRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}