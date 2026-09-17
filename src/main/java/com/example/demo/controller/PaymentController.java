package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @PostMapping
    public ResponseEntity<?> createPayment(
            @RequestBody PaymentRequest request) {

        try {

            return ResponseEntity.ok(
                    paymentService
                        .createPayment(request)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPayment(
            @PathVariable Integer orderId) {

        try {

            return ResponseEntity.ok(
                    paymentService
                        .getPaymentByOrderId(
                            orderId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}
