package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, Integer> {

    List<Product> findByCategory(String category);

    List<Product> findByNameContaining(String keyword);
}