package com.example.demo.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Payment;
import com.example.demo.repository.PaymentRepository;

@Repository
public class PaymentDaoImpl implements PaymentDao {

    @Autowired
    private PaymentRepository paymentRepository;


    // ==========================================
    // 新增 / 修改 Payment
    // ==========================================

    @Override
    public Payment save(Payment payment) {

        return paymentRepository.save(payment);
    }


    // ==========================================
    // 根據 orderId 查付款資料
    // ==========================================

    @Override
    public Optional<Payment> findByOrderId(Integer orderId) {

        return paymentRepository.findByOrderId(orderId);
    }


    // ==========================================
    // 根據 paymentId 查付款資料
    // ==========================================

    @Override
    public Optional<Payment> findById(Integer id) {

        return paymentRepository.findById(id);
    }


    // ==========================================
    // 刪除付款資料
    // ==========================================

    @Override
    public void deleteById(Integer id) {

        paymentRepository.deleteById(id);
    }
}