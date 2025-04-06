package com.example.flowers.dto;

public class OrderProductResponseDTO {
    private Long productId;
    private String productTitle;
    private Integer productPrice; // Новое поле для цены товара
    private Integer quantity;

    public OrderProductResponseDTO() {}

    public OrderProductResponseDTO(Long productId, String productTitle, Integer productPrice, Integer quantity) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Integer getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
