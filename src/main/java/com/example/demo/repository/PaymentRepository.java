package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Integer> {

    // 根據 order_id 查 Payment
    Optional<Payment> findByOrderId(Integer orderId);
}