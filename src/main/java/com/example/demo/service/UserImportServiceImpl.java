package com.example.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.UserImportResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserImportServiceImpl
        implements UserImportService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserImportServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportResponse importUsers(
            MultipartFile file)
            throws IOException {


        // ==========================================
        // 1. 檔案是否存在
        // ==========================================
        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                "請選擇 TXT 檔案"
            );
        }


        // ==========================================
        // 2. 副檔名必須是 txt
        // ==========================================
        String fileName =
                file.getOriginalFilename();


        if (fileName == null
                ||
                !fileName
                    .toLowerCase()
                    .endsWith(".txt")) {

            throw new IllegalArgumentException(
                "只允許上傳 TXT 文字檔"
            );
        }


        // ==========================================
        // 準備暫存
        // ==========================================
        List<User> usersToImport =
                new ArrayList<>();


        // 用來檢查 TXT 自己有沒有重複 Email
        Set<String> emailSet =
                new HashSet<>();


        // ==========================================
        // 3. UTF-8 讀 TXT
        // ==========================================
        try (
            BufferedReader reader =
                new BufferedReader(
                    new InputStreamReader(
                        file.getInputStream(),
                        StandardCharsets.UTF_8
                    )
                )
        ) {

            String line;

            int lineNumber = 0;


            while (
                (line = reader.readLine())
                    != null
            ) {

                lineNumber++;


                // 空白行不處理
                if (line.isBlank()) {
                    continue;
                }


                // ==================================
                // 第一行標題
                // ==================================
                if (
                    lineNumber == 1
                    &&
                    line.toLowerCase()
                        .startsWith("email|")
                ) {

                    continue;
                }


                // ==================================
                // email|password|name|address
                // ==================================
                String[] data =
                        line.split("\\|", -1);


                if (data.length != 4) {

                    throw new IllegalArgumentException(
                        "第 "
                        + lineNumber
                        + " 行格式錯誤，格式必須為："
                        + "email|password|name|address"
                    );
                }


                String email =
                        data[0]
                            .trim()
                            .toLowerCase();

                String password =
                        data[1].trim();

                String name =
                        data[2].trim();

                String address =
                        data[3].trim();


                // ==================================
                // 4. 基本資料驗證
                // ==================================
                if (email.isBlank()) {

                    throw new IllegalArgumentException(
                        "第 "
                        + lineNumber
                        + " 行 Email 不可空白"
                    );
                }


                if (password.isBlank()) {

                    throw new IllegalArgumentException(
                        "第 "
                        + lineNumber
                        + " 行 Password 不可空白"
                    );
                }


                if (name.isBlank()) {

                    throw new IllegalArgumentException(
                        "第 "
                        + lineNumber
                        + " 行 Name 不可空白"
                    );
                }


                // ==================================
                // 5. TXT 本身有沒有重複
                // ==================================
                if (!emailSet.add(email)) {

                    throw new IllegalArgumentException(
                        "匯入失敗：TXT 內 Email 重複："
                        + email
                    );
                }


                // ==================================
                // 6. MySQL 是否已經存在
                // ==================================
                if (
                    userRepository
                        .existsByEmail(email)
                ) {

                    throw new IllegalArgumentException(
                        "匯入失敗：會員 Email 已存在："
                        + email
                    );
                }


                // ==================================
                // 7. 建立 User
                // ==================================
                User user =
                        new User();


                user.setEmail(email);


                // TXT 裡可以放 123456
                // 寫入 MySQL 前 BCrypt
                user.setPassword(
                    passwordEncoder
                        .encode(password)
                );


                user.setName(name);

                user.setAddress(address);


                usersToImport.add(user);
            }
        }


        // ==========================================
        // 8. 沒有任何會員資料
        // ==========================================
        if (usersToImport.isEmpty()) {

            throw new IllegalArgumentException(
                "TXT 沒有可以匯入的會員資料"
            );
        }


        // ==========================================
        // 9. 全部檢查完成才 INSERT
        // ==========================================
        userRepository.saveAll(
                usersToImport
        );


        // 讓 INSERT 在 Transaction 內真正執行
        userRepository.flush();


        // ==========================================
        // 10. 全部成功 → COMMIT
        // ==========================================
        return new UserImportResponse(
                true,
                usersToImport.size(),
                "會員資料匯入成功"
        );
    }
}