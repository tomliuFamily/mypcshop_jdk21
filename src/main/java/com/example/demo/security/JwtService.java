package com.example.demo.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    // ==========================================
    // Token 有效時間
    // ==========================================

    // Access Token：15 分鐘
    private static final long ACCESS_TOKEN_EXPIRATION =
            15 * 60 * 1000L;

    // Refresh Token：7 天
    private static final long REFRESH_TOKEN_EXPIRATION =
            7 * 24 * 60 * 60 * 1000L;


    // ==========================================
    // RSA Key
    // ==========================================

    private final PrivateKey privateKey;

    private final PublicKey publicKey;


    // ==========================================
    // Constructor
    // ==========================================

    public JwtService() {

        try {

            this.privateKey =
                    loadPrivateKey();

            this.publicKey =
                    loadPublicKey();

        } catch (Exception e) {

            throw new RuntimeException(
                    "JWT RSA 金鑰載入失敗",
                    e
            );
        }
    }


    // ==========================================
    // 產生 Access Token
    // 短效 Token
    // ==========================================

    public String generateAccessToken(
            String email) {

        Date now =
                new Date();

        Date expiration =
                new Date(
                        now.getTime()
                        + ACCESS_TOKEN_EXPIRATION
                );


        return Jwts.builder()

                // JWT Subject
                .subject(email)

                // ★ Token 類型
                .claim(
                        "type",
                        "access"
                )

                // 發行時間
                .issuedAt(now)

                // 過期時間
                .expiration(expiration)

                // ★ RS256 私鑰簽章
                .signWith(
                        privateKey,
                        Jwts.SIG.RS256
                )

                .compact();
    }


    // ==========================================
    // 產生 Refresh Token
    // 長效 Token
    // ==========================================

    public String generateRefreshToken(
            String email) {

        Date now =
                new Date();

        Date expiration =
                new Date(
                        now.getTime()
                        + REFRESH_TOKEN_EXPIRATION
                );


        return Jwts.builder()

                .subject(email)

                // ★ Token 類型
                .claim(
                        "type",
                        "refresh"
                )

                .issuedAt(now)

                .expiration(expiration)

                // ★ 一樣使用 Private Key 簽章
                .signWith(
                        privateKey,
                        Jwts.SIG.RS256
                )

                .compact();
    }


    // ==========================================
    // 從 JWT 取得 Email
    // ==========================================

    public String extractEmail(
            String token) {

        Claims claims =
                extractAllClaims(
                        token
                );

        return claims
                .getSubject();
    }


    // ==========================================
    // 取得 Token Type
    //
    // access
    // refresh
    // ==========================================

    public String extractTokenType(
            String token) {

        Claims claims =
                extractAllClaims(
                        token
                );

        return claims.get(
                "type",
                String.class
        );
    }


    // ==========================================
    // 取得 JWT 過期時間
    // ==========================================

    public Date extractExpiration(
            String token) {

        Claims claims =
                extractAllClaims(
                        token
                );

        return claims
                .getExpiration();
    }


    // ==========================================
    // 驗證 Access Token
    // ==========================================

    public boolean isAccessTokenValid(
            String token) {

        try {

            Claims claims =
                    extractAllClaims(
                            token
                    );


            String type =
                    claims.get(
                            "type",
                            String.class
                    );


            Date expiration =
                    claims
                        .getExpiration();


            // 必須同時符合：
            //
            // 1. type = access
            // 2. 尚未過期

            return "access".equals(type)
                    &&
                    expiration != null
                    &&
                    expiration.after(
                            new Date()
                    );

        } catch (Exception e) {

            // 包含：
            //
            // Token 過期
            // Signature 錯誤
            // Token 格式錯誤
            // Public Key 驗證失敗
            //
            // 都視為無效

            return false;
        }
    }


    // ==========================================
    // 驗證 Refresh Token
    // ==========================================

    public boolean isRefreshTokenValid(
            String token) {

        try {

            Claims claims =
                    extractAllClaims(
                            token
                    );


            String type =
                    claims.get(
                            "type",
                            String.class
                    );


            Date expiration =
                    claims
                        .getExpiration();


            // 必須：
            //
            // type = refresh
            // 而且還沒過期

            return "refresh".equals(type)
                    &&
                    expiration != null
                    &&
                    expiration.after(
                            new Date()
                    );

        } catch (Exception e) {

            return false;
        }
    }


    // ==========================================
    // 相容舊程式
    //
    // 如果其他地方還有：
    //
    // jwtService.validateToken(token)
    //
    // 暫時不會報錯
    // ==========================================

    public boolean validateToken(
            String token) {

        return isAccessTokenValid(
                token
        );
    }


    // ==========================================
    // 相容舊程式
    //
    // 如果以前使用：
    //
    // generateToken(email)
    //
    // 現在自動改成產生 Access Token
    // ==========================================

    public String generateToken(
            String email) {

        return generateAccessToken(
                email
        );
    }


    // ==========================================
    // JWT 核心解析
    //
    // ★ Public Key 在這裡驗證 Signature
    // ==========================================

    private Claims extractAllClaims(
            String token) {

        return Jwts
                .parser()

                // ★ 公鑰驗證 JWT Signature
                .verifyWith(
                        publicKey
                )

                .build()

                .parseSignedClaims(
                        token
                )

                .getPayload();
    }


    // ==========================================
    // 載入 Private Key
    // ==========================================

    private PrivateKey loadPrivateKey()
            throws Exception {

        String key =
                readKeyFile(
                        "/keys/private_key.pem"
                );


        key = key
                .replace(
                        "-----BEGIN PRIVATE KEY-----",
                        ""
                )
                .replace(
                        "-----END PRIVATE KEY-----",
                        ""
                )
                .replaceAll(
                        "\\s",
                        ""
                );


        byte[] decoded =
                Base64
                    .getDecoder()
                    .decode(key);


        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(
                        decoded
                );


        KeyFactory keyFactory =
                KeyFactory
                    .getInstance(
                        "RSA"
                    );


        return keyFactory
                .generatePrivate(
                        keySpec
                );
    }


    // ==========================================
    // 載入 Public Key
    // ==========================================

    private PublicKey loadPublicKey()
            throws Exception {

        String key =
                readKeyFile(
                        "/keys/public_key.pem"
                );


        key = key
                .replace(
                        "-----BEGIN PUBLIC KEY-----",
                        ""
                )
                .replace(
                        "-----END PUBLIC KEY-----",
                        ""
                )
                .replaceAll(
                        "\\s",
                        ""
                );


        byte[] decoded =
                Base64
                    .getDecoder()
                    .decode(key);


        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(
                        decoded
                );


        KeyFactory keyFactory =
                KeyFactory
                    .getInstance(
                        "RSA"
                    );


        return keyFactory
                .generatePublic(
                        keySpec
                );
    }


    // ==========================================
    // 從 resources 讀取 Key
    // ==========================================

    private String readKeyFile(
            String path)
            throws Exception {

        try (
            InputStream inputStream =
                    getClass()
                        .getResourceAsStream(
                            path
                        )
        ) {

            if (inputStream == null) {

                throw new IllegalArgumentException(
                        "找不到 JWT Key："
                        + path
                );
            }


            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }
}