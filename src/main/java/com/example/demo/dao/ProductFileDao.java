package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.ProductFile;

public interface ProductFileDao {

    ProductFile save(
        ProductFile productFile
    );

    Optional<ProductFile> findById(
        Integer id
    );

    List<ProductFile> findAll();

    List<ProductFile> findByProductId(
        Integer productId
    );

    void deleteById(
        Integer id
    );
}