package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.ProductFile;
import com.example.demo.repository.ProductFileRepository;

@Repository
public class ProductFileDaoImpl
        implements ProductFileDao {

    private final ProductFileRepository
            productFileRepository;

    public ProductFileDaoImpl(
            ProductFileRepository productFileRepository) {

        this.productFileRepository =
                productFileRepository;
    }


    @Override
    public ProductFile save(
            ProductFile productFile) {

        return productFileRepository
                .save(productFile);
    }


    @Override
    public Optional<ProductFile> findById(
            Integer id) {

        return productFileRepository
                .findById(id);
    }


    @Override
    public List<ProductFile> findAll() {

        return productFileRepository
                .findAllByOrderByUploadTimeDesc();
    }


    @Override
    public List<ProductFile> findByProductId(
            Integer productId) {

        return productFileRepository
                .findByProductIdOrderByUploadTimeDesc(
                    productId
                );
    }


    @Override
    public void deleteById(Integer id) {

        productFileRepository
                .deleteById(id);
    }

}