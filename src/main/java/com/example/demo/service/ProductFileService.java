package com.example.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ProductFileResponse;
import com.example.demo.entity.ProductFile;

public interface ProductFileService {

    ProductFileResponse uploadFile(
        Integer productId,
        MultipartFile file
    ) throws IOException;


    List<ProductFileResponse>
        getAllFiles();


    ProductFile getFile(
        Integer id
    );


    void deleteFile(
        Integer id
    );
}