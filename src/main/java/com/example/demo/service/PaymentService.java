package com.example.demo.service;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(
            PaymentRequest request);

    PaymentResponse getPaymentByOrderId(
            Integer orderId);
}
