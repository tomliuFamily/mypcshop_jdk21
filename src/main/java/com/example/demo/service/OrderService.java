package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.OrderCreateRequest;
import com.example.demo.dto.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(
            OrderCreateRequest request);

    List<OrderResponse> getOrdersByUserId(
            Integer userId);

    OrderResponse getOrderById(
            Integer orderId);
}