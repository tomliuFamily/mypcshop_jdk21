package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.security.JwtService;


@Service
public class UserServiceImpl
        implements UserService {


    // ==========================================
    // User DAO
    // ==========================================

    @Autowired
    private UserDao userDao;


    // ==========================================
    // Password Encoder
    // ==========================================

    @Autowired
    private PasswordEncoder passwordEncoder;


    // ==========================================
    // JWT Service
    // ==========================================

    @Autowired
    private JwtService jwtService;


    // ==========================================
    // 登入
    //
    // Email + Password
    //
    // 登入成功後：
    //
    // 1. 產生 Access Token
    // 2. 產生 Refresh Token
    // ==========================================

    @Override
    public LoginResponse login(
            String email,
            String password) {


        // ======================================
        // 1. 依 Email 查詢 User
        // ======================================

        Optional<User> result =
                userDao.findByEmail(
                        email
                );


        // ======================================
        // 2. 找不到帳號
        // ======================================

        if (result.isEmpty()) {

            return null;
        }


        // ======================================
        // 3. 取得 User Entity
        // ======================================

        User user =
                result.get();


        // ======================================
        // 4. 取得資料庫密碼
        // ======================================

        String storedPassword =
                user.getPassword();


        boolean passwordCorrect =
                false;


        // ======================================
        // 5. BCrypt 密碼驗證
        //
        // 新註冊會員：
        // 使用 BCrypt
        // ======================================

        if (
                storedPassword.startsWith("$2a$")
                ||
                storedPassword.startsWith("$2b$")
                ||
                storedPassword.startsWith("$2y$")
        ) {


            passwordCorrect =
                    passwordEncoder.matches(
                            password,
                            storedPassword
                    );

        } else {


            // ==================================
            // 6. 相容舊的明碼帳號
            //
            // 例如：
            //
            // admin@example.com
            // 123456
            //
            // 舊資料仍然可以登入
            // ==================================

            passwordCorrect =
                    storedPassword.equals(
                            password
                    );
        }


        // ======================================
        // 7. 密碼錯誤
        // ======================================

        if (!passwordCorrect) {

            return null;
        }


        // ======================================
        // 8. 登入成功
        //
        // ★ 產生短效 Access Token
        //
        // 例如：
        // 15 分鐘
        // ======================================

        String accessToken =
                jwtService
                        .generateAccessToken(
                                user.getEmail()
                        );


        // ======================================
        // 9. ★ 產生長效 Refresh Token
        //
        // 例如：
        // 7 天
        // ======================================

        String refreshToken =
                jwtService
                        .generateRefreshToken(
                                user.getEmail()
                        );


        // ======================================
        // 10. 回傳 LoginResponse
        //
        // 注意：
        //
        // 不回傳 password
        //
        // 回傳：
        //
        // id
        // email
        // name
        // address
        // accessToken
        // refreshToken
        // ======================================

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAddress(),
                accessToken,
                refreshToken
        );
    }


    // ==========================================
    // 註冊
    // ==========================================

    @Override
    public User createUser(
            User user) {


        // ======================================
        // 1. Email 是否已存在
        // ======================================

        if (
                userDao.existsByEmail(
                        user.getEmail()
                )
        ) {

            throw new RuntimeException(
                    "此 Email 已經註冊"
            );
        }


        // ======================================
        // 2. BCrypt 加密密碼
        // ======================================

        String encryptedPassword =
                passwordEncoder.encode(
                        user.getPassword()
                );


        // ======================================
        // 3. 將加密後密碼
        // 放回 User Entity
        // ======================================

        user.setPassword(
                encryptedPassword
        );


        // ======================================
        // 4. 儲存 User
        // ======================================

        return userDao.save(
                user
        );
    }
}