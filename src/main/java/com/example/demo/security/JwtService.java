package com.example.demo.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

            // 載入 Private Key
            this.privateKey =
                    loadPrivateKey();


            // 載入 Public Key
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

                // Token 類型
                .claim(
                        "type",
                        "access"
                )

                // 發行時間
                .issuedAt(now)

                // 過期時間
                .expiration(expiration)

                // RS256 私鑰簽章
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

                // Token 類型
                .claim(
                        "type",
                        "refresh"
                )

                // 發行時間
                .issuedAt(now)

                // 過期時間
                .expiration(expiration)

                // Private Key 簽章
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


            // Token 過期
            // Signature 錯誤
            // Token 格式錯誤
            // Public Key 驗證失敗
            //
            // 全部視為無效

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
    // Public Key 在這裡驗證 Signature
    // ==========================================

    private Claims extractAllClaims(
            String token) {


        return Jwts
                .parser()

                // 公鑰驗證 JWT Signature
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
    //
    // ★ 這裡是 Jenkins 版本的主要修改
    // ==========================================

    private PrivateKey loadPrivateKey()
            throws Exception {


        String key;


        // ==========================================
        // 先檢查 Jenkins 是否提供 Secret File
        // ==========================================

        String privateKeyFile =
                System.getenv(
                        "JWT_PRIVATE_KEY_FILE"
                );


        if (privateKeyFile != null
                && !privateKeyFile.isBlank()) {


            // ==========================================
            // Jenkins 執行
            //
            // JWT_PRIVATE_KEY_FILE
            // 會是 Jenkins 建立的暫存檔案路徑
            // ==========================================

            System.out.println(
                    "JWT Private Key：使用 Jenkins Secret File"
            );


            key =
                    Files.readString(
                            Path.of(
                                    privateKeyFile
                            ),
                            StandardCharsets.UTF_8
                    );


        } else {


            // ==========================================
            // Eclipse / 本機執行
            //
            // 沒有 Jenkins 環境變數時
            // 使用 resources 裡面的 private_key.pem
            // ==========================================

            System.out.println(
                    "JWT Private Key：使用 classpath"
            );


            key =
                    readKeyFile(
                            "/keys/private_key.pem"
                    );
        }



        // ==========================================
        // 移除 PEM Header / Footer
        // ==========================================

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



        // ==========================================
        // Base64 Decode
        // ==========================================

        byte[] decoded =
                Base64
                        .getDecoder()
                        .decode(key);



        // ==========================================
        // 建立 PKCS8 Key Spec
        // ==========================================

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(
                        decoded
                );



        // ==========================================
        // RSA KeyFactory
        // ==========================================

        KeyFactory keyFactory =
                KeyFactory
                        .getInstance(
                                "RSA"
                        );



        // ==========================================
        // 產生 PrivateKey
        // ==========================================

        return keyFactory
                .generatePrivate(
                        keySpec
                );
    }



    // ==========================================
    // 載入 Public Key
    //
    // Public Key 不是機密
    // 可以繼續從 resources 讀取
    // ==========================================

    private PublicKey loadPublicKey()
            throws Exception {


        String key =
                readKeyFile(
                        "/keys/public_key.pem"
                );



        // ==========================================
        // 移除 PEM Header / Footer
        // ==========================================

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



        // ==========================================
        // Base64 Decode
        // ==========================================

        byte[] decoded =
                Base64
                        .getDecoder()
                        .decode(key);



        // ==========================================
        // 建立 X509 Key Spec
        // ==========================================

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(
                        decoded
                );



        // ==========================================
        // RSA KeyFactory
        // ==========================================

        KeyFactory keyFactory =
                KeyFactory
                        .getInstance(
                                "RSA"
                        );



        // ==========================================
        // 產生 PublicKey
        // ==========================================

        return keyFactory
                .generatePublic(
                        keySpec
                );
    }



    // ==========================================
    // 從 resources 讀取 Key
    //
    // Eclipse 本機 Private Key
    // Public Key
    // 都可以使用這個方法
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