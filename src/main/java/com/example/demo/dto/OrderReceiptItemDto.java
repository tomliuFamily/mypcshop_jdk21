package com.example.demo.dto;

public class OrderReceiptItemDto {

    private Integer productId;

    private String productName;

    private Integer quantity;

    private Double price;

    private Double subtotal;


    public OrderReceiptItemDto() {
    }


    public OrderReceiptItemDto(
            Integer productId,
            String productName,
            Integer quantity,
            Double price,
            Double subtotal) {

        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}