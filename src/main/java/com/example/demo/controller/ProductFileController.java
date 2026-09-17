package com.example.demo.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ProductFileResponse;
import com.example.demo.entity.ProductFile;
import com.example.demo.service.ProductFileService;


@RestController
@RequestMapping("/api/files")
@CrossOrigin(
    origins =
        "http://localhost:5173"
)
public class ProductFileController {


    private static final String
        ADMIN_EMAIL =
            "admin@example.com";


    private final
        ProductFileService
            productFileService;


    public ProductFileController(
        ProductFileService
            productFileService
    ) {

        this.productFileService =
            productFileService;
    }


    // ==========================================
    // Admin check
    // ==========================================

    private boolean isAdmin(
        Principal principal
    ) {

        if (
            principal == null
        ) {

            return false;
        }


        return ADMIN_EMAIL.equalsIgnoreCase(
            principal.getName()
        );
    }


    // ==========================================
    // Upload
    // 管理員專用
    // ==========================================

    @PostMapping(
        value = "/upload",
        consumes =
            MediaType
                .MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?>
        uploadFile(

            @RequestParam(
                "productId"
            )
            Integer productId,

            @RequestParam(
                "file"
            )
            MultipartFile file,

            Principal principal

        ) {


        if (
            !isAdmin(
                principal
            )
        ) {

            return ResponseEntity
                .status(
                    HttpStatus.FORBIDDEN
                )
                .body(
                    "只有管理員可以上傳商品圖片"
                );
        }


        try {

            ProductFileResponse result =
                productFileService
                    .uploadFile(
                        productId,
                        file
                    );


            return ResponseEntity
                .ok(
                    result
                );


        } catch (
            IllegalArgumentException e
        ) {


            return ResponseEntity
                .badRequest()
                .body(
                    e.getMessage()
                );


        } catch (
            IOException e
        ) {


            return ResponseEntity
                .internalServerError()
                .body(
                    "圖片讀取失敗"
                );
        }
    }


    // ==========================================
    // List
    //
    // 暫時允許讀取
    // ==========================================

    @GetMapping
    public List<
        ProductFileResponse
    > getAllFiles() {


        return productFileService
            .getAllFiles();
    }


    // ==========================================
    // View
    //
    // 商品圖片需要讓網站可以顯示
    // ==========================================

    @GetMapping(
        "/{id}/view"
    )
    public ResponseEntity<byte[]>
        viewFile(

            @PathVariable
            Integer id

        ) {


        ProductFile file =
            productFileService
                .getFile(
                    id
                );


        MediaType mediaType =
            MediaType
                .APPLICATION_OCTET_STREAM;


        if (
            file.getContentType()
            != null
        ) {

            mediaType =
                MediaType
                    .parseMediaType(
                        file
                            .getContentType()
                    );
        }


        return ResponseEntity
            .ok()
            .contentType(
                mediaType
            )
            .body(
                file.getFileData()
            );
    }


    // ==========================================
    // Download
    // ==========================================

    @GetMapping(
        "/{id}/download"
    )
    public ResponseEntity<
        ByteArrayResource
    > downloadFile(

        @PathVariable
        Integer id

    ) {


        ProductFile file =
            productFileService
                .getFile(
                    id
                );


        ByteArrayResource
            resource =
                new ByteArrayResource(
                    file.getFileData()
                );


        ContentDisposition
            disposition =
                ContentDisposition
                    .attachment()
                    .filename(
                        file
                            .getOriginalFileName(),

                        StandardCharsets
                            .UTF_8
                    )
                    .build();


        return ResponseEntity
            .ok()
            .header(

                HttpHeaders
                    .CONTENT_DISPOSITION,

                disposition
                    .toString()
            )
            .contentType(
                MediaType
                    .APPLICATION_OCTET_STREAM
            )
            .contentLength(
                file
                    .getFileSize()
            )
            .body(
                resource
            );
    }


    // ==========================================
    // Delete
    //
    // 管理員專用
    // ==========================================

    @DeleteMapping(
        "/{id}"
    )
    public ResponseEntity<?>
        deleteFile(

            @PathVariable
            Integer id,

            Principal principal

        ) {


        if (
            !isAdmin(
                principal
            )
        ) {

            return ResponseEntity
                .status(
                    HttpStatus.FORBIDDEN
                )
                .body(
                    "只有管理員可以刪除商品圖片"
                );
        }


        try {

            productFileService
                .deleteFile(
                    id
                );


            return ResponseEntity
                .ok(
                    "圖片刪除成功"
                );


        } catch (
            IllegalArgumentException e
        ) {


            return ResponseEntity
                .badRequest()
                .body(
                    e.getMessage()
                );
        }
    }
}