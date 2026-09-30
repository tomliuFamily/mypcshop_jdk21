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
import com.example.demo.entity.User;
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
        // ======================================

        return ResponseEntity.ok(
                response
        );
    }


    // ==========================================
    // 會員註冊
    //
    // POST
    // http://localhost:8080/api/user/create
    //
    // 前端傳入：
    //
    // {
    //     "name": "Kevin",
    //     "email": "kevin@demo.com",
    //     "password": "123456",
    //     "address": "台北市"
    // }
    //
    // UserServiceImpl 負責：
    //
    // 1. 檢查 Email 是否重複
    // 2. BCrypt 加密密碼
    // 3. 儲存 User
    // ==========================================

    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            @RequestBody User user) {


        // ======================================
        // 1. 檢查 Request
        // ======================================

        if (user == null) {

            return ResponseEntity
                    .badRequest()
                    .body("註冊資料不可為空");
        }


        // ======================================
        // 2. 檢查 Email
        // ======================================

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Email 不可為空");
        }


        // ======================================
        // 3. 檢查 Password
        // ======================================

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("密碼不可為空");
        }


        // ======================================
        // 4. 檢查 Name
        // ======================================

        if (user.getName() == null
                || user.getName().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("姓名不可為空");
        }


        try {


            // ==================================
            // 5. 清除前後空白
            // ==================================

            user.setEmail(
                    user.getEmail().trim()
            );


            user.setName(
                    user.getName().trim()
            );


            if (user.getAddress() != null) {

                user.setAddress(
                        user.getAddress().trim()
                );
            }


            // ==================================
            // 6. 呼叫 Service 註冊
            //
            // Service 會處理：
            //
            // existsByEmail()
            // BCrypt 加密
            // userDao.save()
            // ==================================

            User savedUser =
                    userService.createUser(
                            user
                    );


            // ==================================
            // 7. 註冊成功
            //
            // 不回傳 password
            //
            // Map.of 不允許 null，
            // 所以 address 為 null 時改成空字串
            // ==================================

            String address =
                    savedUser.getAddress() == null
                            ? ""
                            : savedUser.getAddress();


            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "id",
                                    savedUser.getId(),

                                    "name",
                                    savedUser.getName(),

                                    "email",
                                    savedUser.getEmail(),

                                    "address",
                                    address,

                                    "message",
                                    "會員註冊成功"
                            )
                    );


        } catch (RuntimeException e) {


            // ==================================
            // 8. 註冊失敗
            //
            // 例如：
            // 此 Email 已經註冊
            // ==================================

            return ResponseEntity
                    .badRequest()
                    .body(
                            e.getMessage()
                    );
        }
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