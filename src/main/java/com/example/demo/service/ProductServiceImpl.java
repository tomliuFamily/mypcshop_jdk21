package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ProductDao;
import com.example.demo.entity.Product;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;


    // =====================================================
    // Constructor Injection
    // =====================================================
    public ProductServiceImpl(
            ProductDao productDao
    ) {

        this.productDao = productDao;
    }


    // =====================================================
    // 查詢全部商品
    // =====================================================
    @Override
    public List<Product> getAllProducts() {

        return productDao.findAll();
    }


    // =====================================================
    // 依商品 ID 查詢
    // =====================================================
    @Override
    public Product getProductById(
            Integer id
    ) {

        return productDao
                .findById(id)
                .orElse(null);
    }


    // =====================================================
    // MyBatis：
    // 商品名稱 + 價格範圍搜尋
    // =====================================================
    @Override
    public List<Product> searchProducts(
            String keyword,
            Double minPrice,
            Double maxPrice
    ) {

        // -------------------------------------------------
        // keyword 去除前後空白
        // -------------------------------------------------
        if (keyword != null) {

            keyword = keyword.trim();

            if (keyword.isEmpty()) {
                keyword = null;
            }
        }


        // -------------------------------------------------
        // 最低價格驗證
        // -------------------------------------------------
        if (
            minPrice != null
            &&
            minPrice < 0
        ) {

            throw new IllegalArgumentException(
                    "最低價格不可小於 0"
            );
        }


        // -------------------------------------------------
        // 最高價格驗證
        // -------------------------------------------------
        if (
            maxPrice != null
            &&
            maxPrice < 0
        ) {

            throw new IllegalArgumentException(
                    "最高價格不可小於 0"
            );
        }


        // -------------------------------------------------
        // 最低價格不可大於最高價格
        // -------------------------------------------------
        if (
            minPrice != null
            &&
            maxPrice != null
            &&
            minPrice > maxPrice
        ) {

            throw new IllegalArgumentException(
                    "最低價格不可大於最高價格"
            );
        }


        return productDao.searchProducts(
                keyword,
                minPrice,
                maxPrice
        );
    }


    // =====================================================
    // JPA：
    // 後端分頁
    // =====================================================
    @Override
    public Page<Product> getProductPage(
            int page,
            int size
    ) {

        // page 從 0 開始
        if (page < 0) {

            throw new IllegalArgumentException(
                    "page 不可小於 0"
            );
        }


        if (size <= 0) {

            throw new IllegalArgumentException(
                    "size 必須大於 0"
            );
        }


        // 防止一次抓太多資料
        if (size > 100) {

            throw new IllegalArgumentException(
                    "size 不可大於 100"
            );
        }


        return productDao.findPage(
                page,
                size
        );
    }
}