package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.OrderCreateRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;


    // 新增訂單
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody OrderCreateRequest request) {

        try {

            OrderResponse order =
                    orderService.createOrder(request);

            return ResponseEntity.ok(order);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // 查詢會員的所有訂單
    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrdersByUser(
            @PathVariable Integer userId) {

        return orderService
                .getOrdersByUserId(userId);
    }


    // 查單一訂單
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable Integer orderId) {

        try {

            return ResponseEntity.ok(
                    orderService.getOrderById(
                            orderId));

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}