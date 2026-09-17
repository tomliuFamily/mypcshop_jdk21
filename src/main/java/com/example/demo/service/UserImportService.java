package com.example.demo.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.UserImportResponse;

public interface UserImportService {

    UserImportResponse importUsers(
            MultipartFile file)
            throws IOException;
}