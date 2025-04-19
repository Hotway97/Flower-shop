package com.example.flowers.dto;

import com.example.flowers.models.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponseDTO {
    private Long id;
    private String status;
    private LocalDateTime createdAt;
    private Long userId;
    private List<OrderProductResponseDTO> orderProducts;
    private Long totalAmount;

    public OrderResponseDTO() {}

    public OrderResponseDTO(Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
        this.userId = order.getUser().getId();
        this.orderProducts = order.getOrderProducts().stream()
                .map(item -> new OrderProductResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getTitle(),     // Наименование товара
                        item.getProduct().getPrice(),     // Цена товара
                        item.getQuantity()))
                .collect(Collectors.toList());
        this.totalAmount = order.getTotalAmount();
    }

    // Геттеры и сеттеры...

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public List<OrderProductResponseDTO> getOrderProducts() {
        return orderProducts;
    }
    public void setOrderProducts(List<OrderProductResponseDTO> orderProducts) {
        this.orderProducts = orderProducts;
    }
    public Long getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
}