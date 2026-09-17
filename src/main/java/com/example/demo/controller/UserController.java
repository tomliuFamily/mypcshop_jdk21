package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.security.JwtService;
import com.example.demo.service.UserService;


@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {


    private final UserService userService;

    private final JwtService jwtService;


    // ==========================================
    // Constructor Injection
    // ==========================================

    public UserController(
            UserService userService,
            JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }


    // ==========================================
    // 登入
    //
    // POST
    // http://localhost:8080/api/user/login
    //
    // 前端傳入：
    //
    // {
    //     "email": "admin@example.com",
    //     "password": "123456"
    // }
    //
    // UserServiceImpl 負責：
    //
    // 1. 驗證 Email
    // 2. 驗證 Password
    // 3. 產生 Access Token
    // 4. 產生 Refresh Token
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {


        // ======================================
        // 1. 呼叫 Service 登入
        // ======================================

        LoginResponse response =
                userService.login(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                );


        // ======================================
        // 2. 登入失敗
        // ======================================

        if (response == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "帳號或密碼錯誤"
                            )
                    );
        }


        // ======================================
        // 3. 登入成功
        //
        // response 已經包含：
        //
        // id
        // email
        // name
        // address
        // accessToken
        // refreshToken
        // ======================================

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // Refresh Token
    //
    // POST
    // http://localhost:8080/api/user/refresh
    //
    // 使用長效 Refresh Token
    // 換新的短效 Access Token
    // ==========================================

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody RefreshTokenRequest request) {


        // ======================================
        // 1. 取得 Refresh Token
        // ======================================

        String refreshToken =
                request.getRefreshToken();


        // ======================================
        // 2. 沒有 Refresh Token
        // ======================================

        if (refreshToken == null
                || refreshToken.isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "沒有 Refresh Token"
                            )
                    );
        }


        // ======================================
        // 3. 驗證 Refresh Token
        //
        // 檢查：
        //
        // Signature
        // type = refresh
        // expiration
        // ======================================

        if (!jwtService.isRefreshTokenValid(
                refreshToken)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Refresh Token 無效或已過期"
                            )
                    );
        }


        // ======================================
        // 4. 從 Refresh Token 取得 Email
        // ======================================

        String email =
                jwtService.extractEmail(
                        refreshToken
                );


        // ======================================
        // 5. 產生新的 Access Token
        // ======================================

        String newAccessToken =
                jwtService.generateAccessToken(
                        email
                );


        // ======================================
        // 6. 回傳新的 Access Token
        // ======================================

        return ResponseEntity.ok(
                Map.of(
                        "accessToken",
                        newAccessToken
                )
        );
    }
}