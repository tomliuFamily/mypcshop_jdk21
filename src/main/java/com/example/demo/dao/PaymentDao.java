package com.example.demo.dao;

import java.util.Optional;

import com.example.demo.entity.Payment;

public interface PaymentDao {

    // 新增 / 修改付款資料
    Payment save(Payment payment);

    // 根據 orderId 查付款資料
    Optional<Payment> findByOrderId(Integer orderId);

    // 根據 paymentId 查付款資料
    Optional<Payment> findById(Integer id);

    // 刪除付款資料
    void deleteById(Integer id);
}