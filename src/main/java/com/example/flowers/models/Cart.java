package com.example.flowers.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cart_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    private long totalPrice = 0;

    // Метод для пересчета общей суммы
    public void calculateTotalPrice() {
        this.totalPrice = products.stream()
                .mapToLong(Product::getPrice)
                .sum();
    }

    // Метод для добавления товара в корзину
    public void addProduct(Product product) {
        this.products.add(product);
        calculateTotalPrice();
    }

    // Метод для удаления товара из корзины
    public void removeProduct(Product product) {
        this.products.remove(product);
        calculateTotalPrice();
    }

    // Метод для очистки корзины
    public void clear() {
        this.products.clear();
        this.totalPrice = 0;
    }
}
