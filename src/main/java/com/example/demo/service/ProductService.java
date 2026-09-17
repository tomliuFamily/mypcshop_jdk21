package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.demo.entity.Product;

public interface ProductService {

    // =====================================================
    // 查詢全部商品
    // =====================================================
    List<Product> getAllProducts();


    // =====================================================
    // 依 ID 查詢商品
    // =====================================================
    Product getProductById(Integer id);


    // =====================================================
    // MyBatis：
    // 商品名稱 + 價格範圍搜尋
    // =====================================================
    List<Product> searchProducts(
            String keyword,
            Double minPrice,
            Double maxPrice
    );


    // =====================================================
    // JPA：
    // 後端分頁
    // =====================================================
    Page<Product> getProductPage(
            int page,
            int size
    );
}