package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;

@Repository
public class ProductDaoImpl implements ProductDao {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;


    // =====================================================
    // Constructor Injection
    // =====================================================
    public ProductDaoImpl(
            ProductRepository productRepository,
            ProductMapper productMapper
    ) {

        this.productRepository = productRepository;

        this.productMapper = productMapper;
    }


    // =====================================================
    // JPA：
    // 查詢全部商品
    // =====================================================
    @Override
    public List<Product> findAll() {

        return productRepository.findAll();
    }


    // =====================================================
    // JPA：
    // 依商品 ID 查詢
    // =====================================================
    @Override
    public Optional<Product> findById(Integer id) {

        return productRepository.findById(id);
    }


    // =====================================================
    // JPA：
    // 儲存商品
    // =====================================================
    @Override
    public Product save(Product product) {

        return productRepository.save(product);
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

        return productMapper.searchProducts(
                keyword,
                minPrice,
                maxPrice
        );
    }


    // =====================================================
    // JPA：
    // 後端分頁
    //
    // page 從 0 開始
    //
    // page=0 → 第 1 頁
    // page=1 → 第 2 頁
    // =====================================================
    @Override
    public Page<Product> findPage(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id").ascending()
                );

        return productRepository.findAll(pageable);
    }
}