package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.StockNotificationRequest;
import com.example.demo.dto.StockNotificationResponse;
import com.example.demo.service.StockNotificationService;

@RestController
@RequestMapping("/api/stock-notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class StockNotificationController {

    private final StockNotificationService
        stockNotificationService;


    public StockNotificationController(
        StockNotificationService
            stockNotificationService
    ) {

        this.stockNotificationService =
            stockNotificationService;
    }


    // ==========================================
    // 新增通知
    // ==========================================

    @PostMapping
    public ResponseEntity<?> create(
        @RequestBody
        StockNotificationRequest request
    ) {

        try {

            return ResponseEntity.ok(
                stockNotificationService
                    .create(request)
            );

        } catch (
            IllegalArgumentException e
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                        "message",
                        e.getMessage()
                    )
                );
        }
    }


    // ==========================================
    // 查詢某會員通知
    // ==========================================

    @GetMapping("/user/{userId}")
    public List<StockNotificationResponse>
        getByUser(
            @PathVariable Integer userId
        ) {

        return stockNotificationService
            .getByUserId(userId);
    }


    // ==========================================
    // 取消通知
    // ==========================================

    @DeleteMapping(
        "/{notificationId}/user/{userId}"
    )
    public ResponseEntity<?> delete(
        @PathVariable
        Integer notificationId,

        @PathVariable
        Integer userId
    ) {

        try {

            stockNotificationService
                .delete(
                    notificationId,
                    userId
                );

            return ResponseEntity.ok(
                Map.of(
                    "message",
                    "已取消到貨通知"
                )
            );

        } catch (
            IllegalArgumentException e
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    Map.of(
                        "message",
                        e.getMessage()
                    )
                );
        }
    }
}