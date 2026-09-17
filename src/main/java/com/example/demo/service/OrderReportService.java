package com.example.demo.service;

public interface OrderReportService {

    byte[] generateOrderReceipt(
            Integer orderId
    ) throws Exception;
}