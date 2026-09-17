package com.example.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.ProductFileDao;
import com.example.demo.dto.ProductFileResponse;
import com.example.demo.entity.ProductFile;

@Service
public class ProductFileServiceImpl
        implements ProductFileService {

    // 10MB
    private static final long MAX_FILE_SIZE =
            10L * 1024 * 1024;

    private final ProductFileDao
            productFileDao;


    public ProductFileServiceImpl(
            ProductFileDao productFileDao) {

        this.productFileDao =
                productFileDao;
    }


    // ==========================================
    // 上傳
    // ==========================================

    @Override
    @Transactional
    public ProductFileResponse uploadFile(
            Integer productId,
            MultipartFile file)
            throws IOException {


        if (productId == null) {

            throw new IllegalArgumentException(
                    "請先選擇商品"
            );
        }


        if (
            file == null
            ||
            file.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "請選擇要上傳的圖片"
            );
        }


        if (
            file.getSize()
            > MAX_FILE_SIZE
        ) {

            throw new IllegalArgumentException(
                    "單一檔案不可超過 10MB"
            );
        }


        String contentType =
                file.getContentType();


        if (
            contentType == null
            ||
            !(
                contentType.equals("image/jpeg")
                ||
                contentType.equals("image/png")
                ||
                contentType.equals("image/webp")
            )
        ) {

            throw new IllegalArgumentException(
                    "只允許 JPG、PNG、WEBP 圖片"
            );
        }


        ProductFile productFile =
                new ProductFile();


        productFile.setProductId(
                productId
        );


        productFile.setOriginalFileName(
                file.getOriginalFilename()
        );


        productFile.setContentType(
                contentType
        );


        productFile.setFileSize(
                file.getSize()
        );


        // ★ Binary
        productFile.setFileData(
                file.getBytes()
        );


        productFile.setUploadTime(
                LocalDateTime.now()
        );


        ProductFile saved =
                productFileDao
                    .save(productFile);


        return convertToResponse(
                saved
        );
    }


    // ==========================================
    // 查詢全部
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductFileResponse>
            getAllFiles() {

        return productFileDao
                .findAll()
                .stream()
                .map(
                    this::convertToResponse
                )
                .toList();
    }


    // ==========================================
    // 單一檔案
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public ProductFile getFile(
            Integer id) {

        return productFileDao
                .findById(id)
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "找不到檔案 id=" + id
                        )
                );
    }


    // ==========================================
    // 刪除
    // ==========================================

    @Override
    @Transactional
    public void deleteFile(
            Integer id) {

        ProductFile file =
                getFile(id);

        productFileDao
                .deleteById(
                    file.getId()
                );
    }


    // ==========================================
    // Entity → DTO
    // ==========================================

    private ProductFileResponse
            convertToResponse(
                ProductFile file) {

        ProductFileResponse response =
                new ProductFileResponse();


        response.setId(
                file.getId()
        );

        response.setProductId(
                file.getProductId()
        );

        response.setOriginalFileName(
                file.getOriginalFileName()
        );

        response.setContentType(
                file.getContentType()
        );

        response.setFileSize(
                file.getFileSize()
        );

        response.setUploadTime(
                file.getUploadTime()
        );


        return response;
    }
}