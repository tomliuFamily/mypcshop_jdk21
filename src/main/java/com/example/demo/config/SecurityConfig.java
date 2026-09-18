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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtAuthenticationFilter;


@Configuration
public class SecurityConfig {


    // ==============================================
    // JWT Filter
    // ==============================================
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }


    // ==============================================
    // Spring Security 主設定
    // ==============================================
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {


        http


            // ==========================================
            // CORS
            // React localhost:5173
            // 可以呼叫 Spring Boot localhost:8080
            // ==========================================
            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource()
                )
            )


            // ==========================================
            // REST API + JWT
            //
            // JWT 不使用傳統 CSRF Token
            // ==========================================
            .csrf(csrf ->
                csrf.disable()
            )


            // ==========================================
            // Session
            //
            // JWT 採 STATELESS
            // Server 不保存登入 Session
            // ==========================================
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )


            // ==========================================
            // API 權限設定
            // ==========================================
            .authorizeHttpRequests(auth -> auth


                // ======================================
                // React CORS Preflight
                // ======================================
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                )
                .permitAll()


                // ======================================
                // 登入
                //
                // 不需要 JWT
                // ======================================
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user/login"
                )
                .permitAll()


                // ======================================
                // 會員註冊
                //
                // 不需要 JWT
                // ======================================
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user",
                    "/api/user/create"
                )
                .permitAll()


                // ======================================
                // Refresh Token
                //
                // Access Token 過期時使用
                // ======================================
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user/refresh"
                )
                .permitAll()


                // ======================================
                // 商品公開查詢
                //
                // 不需要登入
                // ======================================
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/products/**"
                )
                .permitAll()


                // ======================================
                // Jenkins CI/CD 部署測試
                //
                // 不需要登入
                //
                // 用來確認：
                // GitHub
                //   ↓
                // Jenkins
                //   ↓
                // Maven Package
                //   ↓
                // Deploy
                //   ↓
                // Spring Boot 新版本
                // ======================================
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/deploy-test"
                )
                .permitAll()


                // ======================================
                // 商品圖片 / 檔案公開查詢
                // ======================================
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/product-files/**"
                )
                .permitAll()


                // ======================================
                // 留言公開查詢
                // ======================================
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/messages/**"
                )
                .permitAll()


                // ======================================
                // 首頁累計人數公開查詢
                // ======================================
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/stats/visits"
                )
                .permitAll()


                // ======================================
                // WebSocket
                // ======================================
                .requestMatchers(
                    "/ws/**"
                )
                .permitAll()


                // ======================================
                // 管理員 TXT 會員匯入
                //
                // Spring Security:
                // hasRole("ADMIN")
                //
                // 實際會檢查 ROLE_ADMIN
                // ======================================
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/admin/users/import"
                )
                .hasRole("ADMIN")


                // ======================================
                // 其他所有 API
                //
                // 例如：
                // 訂單
                // 付款
                // 統計 POST
                // 會員相關保護 API
                //
                // 全部必須通過 JWT 驗證
                // ======================================
                .anyRequest()
                .authenticated()
            )


            // ==========================================
            // JWT Authentication Filter
            //
            // 放在 UsernamePasswordAuthenticationFilter
            // 前面
            // ==========================================
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }


    // ==============================================
    // BCrypt Password Encoder
    // ==============================================
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ==============================================
    // CORS Configuration
    // ==============================================
    @Bean
    public CorsConfigurationSource
            corsConfigurationSource() {


        CorsConfiguration configuration =
                new CorsConfiguration();


        // ==========================================
        // 允許 React 前端
        // ==========================================
        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173"
            )
        );


        // ==========================================
        // 允許 HTTP Methods
        // ==========================================
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


        // ==========================================
        // 允許 Request Headers
        //
        // Authorization：
        // Bearer JWT Token
        // ==========================================
        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type",
                "Accept"
            )
        );


        // ==========================================
        // 前端可以讀取的 Response Header
        // ==========================================
        configuration.setExposedHeaders(
            List.of(
                "Authorization"
            )
        );


        // ==========================================
        // 允許 Credentials
        // ==========================================
        configuration.setAllowCredentials(true);


        // ==========================================
        // 套用到所有路徑
        // ==========================================
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
            "/**",
            configuration
        );


        return source;
    }
}