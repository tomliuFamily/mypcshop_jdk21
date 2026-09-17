package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Order;

public interface OrderRepository
        extends JpaRepository<Order, Integer> {

    // 查詢某位會員的全部訂單
    List<Order> findByUserId(Integer userId);

    // 查詢某位會員訂單，新的在前面
    List<Order> findByUserIdOrderByOrderDateDesc(
            Integer userId
    );

    // 依照訂單狀態查詢
    List<Order> findByStatus(
            String status
    );

    // 查詢某會員特定狀態的訂單
    List<Order> findByUserIdAndStatus(
            Integer userId,
            String status
    );
}