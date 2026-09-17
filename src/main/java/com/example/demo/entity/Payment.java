package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    // ==========================================
    // Payment ID
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    // ==========================================
    // 訂單 ID
    //
    // 對應：
    // payments.order_id
    // ==========================================

    @Column(name = "order_id", nullable = false)
    private Integer orderId;


    // ==========================================
    // 付款方式
    //
    // CREDIT_CARD
    // BANK_TRANSFER
    // CASH_ON_DELIVERY
    // ==========================================

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;


    // ==========================================
    // 付款金額
    // ==========================================

    @Column(name = "amount", nullable = false)
    private Double amount;


    // ==========================================
    // 付款狀態
    //
    // UNPAID
    // PENDING
    // PAID
    // FAILED
    // ==========================================

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;


    // ==========================================
    // 實際付款完成時間
    // ==========================================

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;


    // ==========================================
    // 無參數建構子
    // JPA 需要
    // ==========================================

    public Payment() {
    }


    // ==========================================
    // Getter / Setter
    // ==========================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }


    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}