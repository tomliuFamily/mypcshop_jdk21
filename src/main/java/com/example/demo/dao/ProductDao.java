package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.example.demo.entity.Product;

public interface ProductDao {

    // =====================================================
    // JPA：查詢全部商品
    // =====================================================
    List<Product> findAll();


    // =====================================================
    // JPA：依 ID 查詢商品
    // =====================================================
    Optional<Product> findById(Integer id);


    // =====================================================
    // JPA：儲存商品
    //
    // OrderServiceImpl 會使用
    // 用來扣庫存、增加 salesCount
    // =====================================================
    Product save(Product product);


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
    Page<Product> findPage(
            int page,
            int size
    );
}