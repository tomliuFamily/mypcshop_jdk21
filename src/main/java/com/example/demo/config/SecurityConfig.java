package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication
        .UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtAuthenticationFilter;


@Configuration
public class SecurityConfig {


    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;


    // ==========================================
    // Constructor Injection
    // ==========================================

    public SecurityConfig(
            JwtAuthenticationFilter
                    jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }


    // ==========================================
    // Spring Security Filter Chain
    // ==========================================

    @Bean
    public SecurityFilterChain
            securityFilterChain(
                    HttpSecurity http
            ) throws Exception {


        http


            // ==================================
            // CORS
            // ==================================

            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource()
                )
            )


            // ==================================
            // 關閉 CSRF
            //
            // REST API + JWT
            // 不使用 Session CSRF Token
            // ==================================

            .csrf(csrf ->
                csrf.disable()
            )


            // ==================================
            // 關閉預設 Login Form
            // ==================================

            .formLogin(form ->
                form.disable()
            )


            // ==================================
            // 關閉 HTTP Basic
            // ==================================

            .httpBasic(basic ->
                basic.disable()
            )


            // ==================================
            // JWT 採用無狀態 Session
            // ==================================

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )


            // ==================================
            // API 權限設定
            // ==================================

            .authorizeHttpRequests(auth -> auth


                // ==============================
                // React CORS Preflight
                // ==============================

                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                )
                .permitAll()


                // ==============================
                // 會員登入
                //
                // 不需要 JWT
                // ==============================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user/login"
                )
                .permitAll()


                // ==============================
                // 會員註冊
                //
                // 不需要 JWT
                // ==============================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user",
                    "/api/user/create"
                )
                .permitAll()


                // ==============================
                // Refresh Token
                //
                // Access Token 過期時使用
                // 不需要 Access Token
                // ==============================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user/refresh"
                )
                .permitAll()


                // ==============================
                // 商品公開查詢
                //
                // GET /api/products
                // GET /api/products/search
                // GET /api/products/page
                // GET /api/products/{id}
                // ==============================

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/products/**"
                )
                .permitAll()


                // ==============================
                // Jenkins CI/CD 部署測試
                //
                // 不需要登入
                // ==============================

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/deploy-test"
                )
                .permitAll()


                // ==============================
                // 商品圖片與檔案公開查詢
                //
                // Controller 實際路徑：
                // /api/files
                //
                // 只開放 GET：
                //
                // GET /api/files
                // GET /api/files/{id}/view
                // GET /api/files/{id}/download
                //
                // POST 與 DELETE 不會被公開
                // ==============================

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/files/**"
                )
                .permitAll()


                // ==============================
                // 留言公開查詢
                //
                // GET /api/messages
                // GET /api/messages/page
                // GET 附件圖片
                // ==============================

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/messages/**"
                )
                .permitAll()


                // ==============================
                // 首頁累計瀏覽人次
                //
                // GET 可以公開查詢
                // POST 增加人次仍需要 JWT
                // ==============================

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/stats/visits"
                )
                .permitAll()


                // ==============================
                // WebSocket
                //
                // 允許建立 WebSocket 連線
                // ==============================

                .requestMatchers(
                    "/ws/**"
                )
                .permitAll()


                // ==============================
                // 管理員 TXT 會員匯入
                //
                // 必須具備 ROLE_ADMIN
                // ==============================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/admin/users/import"
                )
                .hasRole("ADMIN")


                // ==============================
                // 其他所有 API
                //
                // 必須經過 JWT 驗證
                //
                // 例如：
                //
                // POST /api/orders
                // GET  /api/orders/**
                // POST /api/payments
                // POST /api/messages/upload
                // POST /api/messages/{id}/reply
                // POST /api/stock-notifications
                // DELETE /api/stock-notifications/**
                // POST /api/files/upload
                // DELETE /api/files/{id}
                // POST /api/stats/visit
                // ==============================

                .anyRequest()
                .authenticated()
            )


            // ==================================
            // JWT Authentication Filter
            //
            // JWT Filter 必須放在
            // UsernamePasswordAuthenticationFilter
            // 前面
            // ==================================

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }


    // ==========================================
    // BCrypt Password Encoder
    // ==========================================

    @Bean
    public PasswordEncoder
            passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ==========================================
    // CORS Configuration
    // ==========================================

    @Bean
    public CorsConfigurationSource
            corsConfigurationSource() {


        CorsConfiguration configuration =
                new CorsConfiguration();


        // ======================================
        // 允許 React 前端
        // ======================================

        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173"
            )
        );


        // ======================================
        // 允許 HTTP Methods
        // ======================================

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
            )
        );


        // ======================================
        // 允許 Request Headers
        //
        // 包含 Authorization Bearer Token
        // ======================================

        configuration.setAllowedHeaders(
            List.of(
                "*"
            )
        );


        // ======================================
        // 允許前端讀取 Response Headers
        //
        // Content-Disposition 用於下載檔案
        // ======================================

        configuration.setExposedHeaders(
            List.of(
                "Authorization",
                "Content-Disposition"
            )
        );


        // ======================================
        // 是否允許攜帶 Cookie
        //
        // 目前 JWT 存在 sessionStorage，
        // 不是使用 Cookie。
        //
        // 保留 true 不影響 Bearer Token。
        // ======================================

        configuration.setAllowCredentials(
            true
        );


        // ======================================
        // Preflight 快取時間
        //
        // 單位：秒
        // ======================================

        configuration.setMaxAge(
            3600L
        );


        // ======================================
        // 套用至全部 API
        // ======================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
            "/**",
            configuration
        );


        return source;
    }
}