package com.example.demo.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RsaKeyGenerator {

    public static void main(String[] args) throws Exception {

        // =====================================================
        // 建立 RSA KeyPair Generator
        // =====================================================

        KeyPairGenerator keyPairGenerator =
                KeyPairGenerator.getInstance("RSA");


        // =====================================================
        // RSA 2048 bit
        // =====================================================

        keyPairGenerator.initialize(2048);


        // =====================================================
        // 產生公私鑰
        // =====================================================

        KeyPair keyPair =
                keyPairGenerator.generateKeyPair();


        PrivateKey privateKey =
                keyPair.getPrivate();


        PublicKey publicKey =
                keyPair.getPublic();


        // =====================================================
        // 建立 keys 資料夾
        // =====================================================

        Path keysDirectory =
                Path.of(
                    "src",
                    "main",
                    "resources",
                    "keys"
                );


        Files.createDirectories(
                keysDirectory
        );


        // =====================================================
        // Private Key
        // =====================================================

        String privateKeyPem =
                convertPrivateKeyToPem(
                        privateKey
                );


        // =====================================================
        // Public Key
        // =====================================================

        String publicKeyPem =
                convertPublicKeyToPem(
                        publicKey
                );


        // =====================================================
        // 寫入 private_key.pem
        // =====================================================

        Files.writeString(

                keysDirectory.resolve(
                        "private_key.pem"
                ),

                privateKeyPem,

                StandardCharsets.UTF_8
        );


        // =====================================================
        // 寫入 public_key.pem
        // =====================================================

        Files.writeString(

                keysDirectory.resolve(
                        "public_key.pem"
                ),

                publicKeyPem,

                StandardCharsets.UTF_8
        );


        System.out.println(
                "========================================"
        );

        System.out.println(
                "RSA Key Pair 建立成功"
        );

        System.out.println(
                "Private Key:"
        );

        System.out.println(
                keysDirectory.resolve(
                        "private_key.pem"
                ).toAbsolutePath()
        );

        System.out.println(
                "Public Key:"
        );

        System.out.println(
                keysDirectory.resolve(
                        "public_key.pem"
                ).toAbsolutePath()
        );

        System.out.println(
                "========================================"
        );
    }


    // =========================================================
    // Private Key → PEM
    // =========================================================

    private static String convertPrivateKeyToPem(
            PrivateKey privateKey
    ) throws IOException {

        String base64 =
                Base64.getMimeEncoder(
                        64,
                        "\n".getBytes(
                                StandardCharsets.UTF_8
                        )
                )
                .encodeToString(
                        privateKey.getEncoded()
                );


        return """
                -----BEGIN PRIVATE KEY-----
                %s
                -----END PRIVATE KEY-----
                """.formatted(base64);
    }


    // =========================================================
    // Public Key → PEM
    // =========================================================

    private static String convertPublicKeyToPem(
            PublicKey publicKey
    ) throws IOException {

        String base64 =
                Base64.getMimeEncoder(
                        64,
                        "\n".getBytes(
                                StandardCharsets.UTF_8
                        )
                )
                .encodeToString(
                        publicKey.getEncoded()
                );


        return """
                -----BEGIN PUBLIC KEY-----
                %s
                -----END PUBLIC KEY-----
                """.formatted(base64);
    }
}