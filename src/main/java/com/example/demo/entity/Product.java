package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;


    private String name;


    private Double price;


    private String category;


    private String description;


    private String image;


    // ==========================================
    // 庫存
    // ==========================================

    @Column(
        nullable = false
    )
    private Integer stock = 0;


    // ==========================================
    // 累計銷售量
    // ==========================================

    @Column(
        name = "sales_count",
        nullable = false
    )
    private Integer salesCount = 1000;


    // ==========================================
    // Constructor
    // ==========================================

    public Product() {
    }


    // ==========================================
    // Getter / Setter
    // ==========================================

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Double getPrice() {
        return price;
    }


    public void setPrice(Double price) {
        this.price = price;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(
            String category) {

        this.category =
                category;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(
            String description) {

        this.description =
                description;
    }


    public String getImage() {
        return image;
    }


    public void setImage(
            String image) {

        this.image =
                image;
    }


    public Integer getStock() {
        return stock;
    }


    public void setStock(
            Integer stock) {

        this.stock =
                stock;
    }


    public Integer getSalesCount() {
        return salesCount;
    }


    public void setSalesCount(
            Integer salesCount) {

        this.salesCount =
                salesCount;
    }
}