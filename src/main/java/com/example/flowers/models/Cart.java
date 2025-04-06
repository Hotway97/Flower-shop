package com.example.flowers.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
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

    // Используем EAGER-загрузку, чтобы избежать LazyInitializationException в шаблонах
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();

    private long totalPrice = 0;

    // Метод для пересчёта общей суммы корзины
    public void calculateTotalPrice() {
        this.totalPrice = cartItems.stream()
                .mapToLong(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    // Метод для добавления товара в корзину
    public void addProduct(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                calculateTotalPrice();
                return;
            }
        }
        CartItem newItem = new CartItem();
        newItem.setCart(this);
        newItem.setProduct(product);
        newItem.setQuantity(1);
        cartItems.add(newItem);
        calculateTotalPrice();
    }

    // Метод для удаления одного экземпляра товара из корзины
    public void removeProduct(Product product) {
        for (Iterator<CartItem> it = cartItems.iterator(); it.hasNext(); ) {
            CartItem item = it.next();
            if (item.getProduct().getId().equals(product.getId())) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    it.remove();
                }
                break;
            }
        }
        calculateTotalPrice();
    }

    // Метод для очистки корзины
    public void clear() {
        cartItems.clear();
        totalPrice = 0;
    }
}
