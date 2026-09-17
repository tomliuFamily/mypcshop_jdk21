package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
    name = "stock_notifications",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_product",
            columnNames = {
                "user_id",
                "product_id"
            }
        )
    }
)
public class StockNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "product_id",
        nullable = false
    )
    private Product product;


    @Column(name = "target_price")
    private BigDecimal targetPrice;


    @Column(
        nullable = false,
        length = 20
    )
    private String status = "WAITING";


    @Column(
        name = "created_at",
        nullable = false
    )
    private LocalDateTime createdAt;


    public StockNotification() {
    }


    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = "WAITING";
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(BigDecimal targetPrice) {
        this.targetPrice = targetPrice;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
        LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }
}