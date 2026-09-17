package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.StockNotificationRequest;
import com.example.demo.dto.StockNotificationResponse;

public interface StockNotificationService {

    StockNotificationResponse create(
        StockNotificationRequest request
    );


    List<StockNotificationResponse>
        getByUserId(
            Integer userId
        );


    void delete(
        Integer notificationId,
        Integer userId
    );
}