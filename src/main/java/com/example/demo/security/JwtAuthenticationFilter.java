package com.example.demo.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService) {

        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");


        // ==========================================
        // 沒有 Bearer Token
        // 繼續交給 Spring Security
        // ==========================================
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // ==========================================
        // 去掉 "Bearer "
        // ==========================================
        String jwt =
                authHeader.substring(7);


        try {

            // ======================================
            // 驗證 Access Token
            // ======================================
            if (jwtService.isAccessTokenValid(jwt)) {

                String email =
                        jwtService.extractEmail(jwt);


                // ==================================
                // ★ 管理員 / 一般會員角色
                //
                // 目前先依 email 判斷
                // ==================================
                String role;

                if ("admin@example.com"
                        .equalsIgnoreCase(email)) {

                    role = "ROLE_ADMIN";

                } else {

                    role = "ROLE_USER";
                }


                // ==================================
                // 建立 Spring Security
                // Authentication
                // ==================================
                UsernamePasswordAuthenticationToken
                        authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(
                                    new SimpleGrantedAuthority(
                                        role
                                    )
                                )
                        );


                // ==================================
                // 放進 SecurityContext
                // ==================================
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );
            }

        } catch (Exception e) {

            SecurityContextHolder
                    .clearContext();
        }


        filterChain.doFilter(
                request,
                response
        );
    }
}