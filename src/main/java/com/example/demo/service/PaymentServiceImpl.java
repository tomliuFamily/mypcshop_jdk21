package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.OrderDao;
import com.example.demo.dao.PaymentDao;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;

@Service
public class PaymentServiceImpl
        implements PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private OrderDao orderDao;


    // ============================================
    // 建立付款
    // ============================================

    @Override
    @Transactional
    public PaymentResponse createPayment(
            PaymentRequest request) {

        // ========================================
        // 1. 查詢訂單
        // ========================================

        Order order =
                orderDao
                        .findOrderById(
                                request.getOrderId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "找不到訂單"
                                )
                        );


        // ========================================
        // 2. 防止重複付款
        // ========================================

        if (paymentDao
                .findByOrderId(
                        order.getId()
                )
                .isPresent()) {

            throw new RuntimeException(
                    "此訂單已有付款資料"
            );
        }


        // ========================================
        // 3. 取得付款方式
        // ========================================

        String method =
                request.getPaymentMethod();


        if (method == null) {

            throw new RuntimeException(
                    "付款方式不可為空"
            );
        }


        // ========================================
        // 4. 檢查付款方式
        // ========================================

        if (!method.equals("CREDIT_CARD")
                &&
            !method.equals("BANK_TRANSFER")
                &&
            !method.equals("CASH_ON_DELIVERY")) {

            throw new RuntimeException(
                    "不支援的付款方式"
            );
        }


        // ========================================
        // 5. 建立 Payment
        // ========================================

        Payment payment =
                new Payment();


        payment.setOrderId(
                order.getId()
        );


        payment.setPaymentMethod(
                method
        );


        // 金額一定由後端訂單取得
        payment.setAmount(
                order.getTotalAmount()
        );


        // ========================================
        // 6. 模擬付款流程
        // ========================================
        //
        // CREDIT_CARD
        //      → 立即 PAID
        //
        // BANK_TRANSFER
        //      → PENDING
        //
        // CASH_ON_DELIVERY
        //      → PENDING
        // ========================================

        if (method.equals("CREDIT_CARD")) {


            payment.setPaymentStatus(
                    "PAID"
            );


            payment.setPaymentDate(
                    LocalDateTime.now()
            );


            // 訂單同步改為 PAID
            order.setStatus(
                    "PAID"
            );


        } else {


            payment.setPaymentStatus(
                    "PENDING"
            );


            payment.setPaymentDate(
                    null
            );
        }


        // ========================================
        // 7. 儲存付款資料
        // ========================================

        Payment saved =
                paymentDao.save(
                        payment
                );


        // ========================================
        // 8. 信用卡付款成功
        //    同步更新 Order
        // ========================================

        if (method.equals("CREDIT_CARD")) {

            orderDao.saveOrder(
                    order
            );
        }


        // ========================================
        // 9. 回傳 PaymentResponse
        // ========================================

        return convertToResponse(
                saved
        );
    }


    // ============================================
    // 查詢付款資料
    // ============================================

    @Override
    public PaymentResponse getPaymentByOrderId(
            Integer orderId) {


        Payment payment =
                paymentDao
                        .findByOrderId(
                                orderId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "找不到付款資料"
                                )
                        );


        return convertToResponse(
                payment
        );
    }


    // ============================================
    // Entity → DTO
    // ============================================

    private PaymentResponse convertToResponse(
            Payment payment) {


        PaymentResponse response =
                new PaymentResponse();


        response.setId(
                payment.getId()
        );


        response.setOrderId(
                payment.getOrderId()
        );


        response.setPaymentMethod(
                payment.getPaymentMethod()
        );


        response.setAmount(
                payment.getAmount()
        );


        response.setPaymentStatus(
                payment.getPaymentStatus()
        );


        response.setPaymentDate(
                payment.getPaymentDate()
        );


        return response;
    }
}