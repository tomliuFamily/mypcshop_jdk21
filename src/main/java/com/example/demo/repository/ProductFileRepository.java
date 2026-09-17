package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.ProductFile;

public interface ProductFileRepository
        extends JpaRepository<ProductFile, Integer> {

    List<ProductFile>
        findAllByOrderByUploadTimeDesc();

    List<ProductFile>
        findByProductIdOrderByUploadTimeDesc(
            Integer productId
        );
}