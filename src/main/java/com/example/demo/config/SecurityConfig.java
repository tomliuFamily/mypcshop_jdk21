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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // ==========================================
            // CORS
            // ==========================================
            .cors(cors ->
                cors.configurationSource(
                    corsConfigurationSource()
                )
            )


            // ==========================================
            // REST + JWT
            // ==========================================
            .csrf(csrf ->
                csrf.disable()
            )


            // ==========================================
            // JWT 不使用 Session
            // ==========================================
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )


            // ==========================================
            // API 權限
            // ==========================================
            .authorizeHttpRequests(auth -> auth


                // React CORS Preflight
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                )
                .permitAll()


                // 登入
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user/login"
                )
                .permitAll()


                // 註冊
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user",
                    "/api/user/create"
                )
                .permitAll()


                // Refresh Token
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/user/refresh"
                )
                .permitAll()


                // 商品公開查詢
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/products/**"
                )
                .permitAll()


                // 商品圖片 / 檔案公開查詢
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/product-files/**"
                )
                .permitAll()


                // 留言公開查詢
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/messages/**"
                )
                .permitAll()


                // 首頁累計人數公開查詢
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/stats/visits"
                )
                .permitAll()


                // WebSocket
                .requestMatchers(
                    "/ws/**"
                )
                .permitAll()


                // ======================================
                // ★ 管理員 TXT 會員匯入
                //
                // 只有 ROLE_ADMIN 可以使用
                // ======================================
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/admin/users/import"
                )
                .hasRole("ADMIN")


                // ======================================
                // 其他 API
                //
                // 訂單、付款、統計 POST...
                // 都必須登入
                // ======================================
                .anyRequest()
                .authenticated()
            )


            // ==========================================
            // JWT Filter
            // ==========================================
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );


        return http.build();
    }


    // ==============================================
    // BCrypt
    // ==============================================
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // ==============================================
    // CORS
    // ==============================================
    @Bean
    public CorsConfigurationSource
            corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173"
            )
        );


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


        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type",
                "Accept"
            )
        );


        configuration.setExposedHeaders(
            List.of(
                "Authorization"
            )
        );


        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
            "/**",
            configuration
        );


        return source;
    }
}