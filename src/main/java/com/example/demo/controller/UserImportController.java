package com.example.demo.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.UserImportResponse;
import com.example.demo.service.UserImportService;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserImportController {

    private final UserImportService
            userImportService;


    public UserImportController(
            UserImportService userImportService) {

        this.userImportService =
                userImportService;
    }


    @PostMapping(
        value = "/import",
        consumes = "multipart/form-data"
    )
    public ResponseEntity<?> importUsers(
            @RequestParam("file")
            MultipartFile file) {

        try {

            UserImportResponse response =
                    userImportService
                        .importUsers(file);


            return ResponseEntity.ok(
                    response
            );


        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        Map.of(
                            "success", false,
                            "message",
                            e.getMessage()
                        )
                    );


        } catch (IOException e) {

            return ResponseEntity
                    .status(
                        HttpStatus
                            .INTERNAL_SERVER_ERROR
                    )
                    .body(
                        Map.of(
                            "success", false,
                            "message",
                            "TXT 檔案讀取失敗"
                        )
                    );
        }
    }
}