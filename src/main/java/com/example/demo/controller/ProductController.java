package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;


    // =====================================================
    // Constructor Injection
    // =====================================================
    public ProductController(
            ProductService productService
    ) {

        this.productService = productService;
    }


    // =====================================================
    // 1. 查詢全部商品
    //
    // GET
    // /api/products
    // =====================================================
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Product>>
            getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }


    // =====================================================
    // 2. MyBatis 搜尋
    //
    // 可搜尋：
    //
    // keyword
    // minPrice
    // maxPrice
    //
    // 範例：
    //
    // /api/products/search?keyword=滑鼠
    //
    // /api/products/search
    // ?minPrice=500&maxPrice=1500
    //
    // /api/products/search
    // ?keyword=滑鼠&minPrice=500&maxPrice=1500
    // =====================================================
    @GetMapping(
            value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> searchProducts(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice

    ) {

        try {

            List<Product> products =
                    productService.searchProducts(
                            keyword,
                            minPrice,
                            maxPrice
                    );

            return ResponseEntity.ok(products);

        }
        catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =====================================================
    // 3. 後端分頁
    //
    // GET
    //
    // /api/products/page?page=0&size=4
    //
    // page 從 0 開始
    //
    // page=0 → 第 1 頁
    // page=1 → 第 2 頁
    // =====================================================
    @GetMapping(
            value = "/page",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getProductPage(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "4")
            int size

    ) {

        try {

            Page<Product> result =
                    productService.getProductPage(
                            page,
                            size
                    );

            return ResponseEntity.ok(result);

        }
        catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =====================================================
    // 4. 依 ID 查詢商品
    //
    // GET
    // /api/products/1
    // =====================================================
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getProductById(

            @PathVariable
            Integer id

    ) {

        Product product =
                productService.getProductById(id);


        if (product == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity.ok(product);
    }
}