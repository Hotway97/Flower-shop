package com.example.flowers.services;

import com.example.flowers.models.*;
import com.example.flowers.repositories.CartRepository;
import com.example.flowers.repositories.OrderRepository;
import com.example.flowers.repositories.ProductRepository;
import com.example.flowers.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ProductRepository productRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Order confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setStatus("completed");

        Long userId = order.getUser().getId();

        // Добавляем товары пользователю
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Long productId = orderProduct.getProduct().getId();
            enrollUserInProduct(userId, productId);
        }

        // Очищаем корзину пользователя полностью через итератор (для orphanRemoval)
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            Iterator<CartItem> it = cart.getCartItems().iterator();
            while (it.hasNext()) {
                CartItem item = it.next();
                item.setCart(null); // удаляем связь
                it.remove();
            }
            cart.setTotalPrice(0);
            cartRepository.save(cart);
        }

        System.out.println("Корзина пользователя очищена");
        return orderRepository.save(order);
    }

    @Transactional
    public void enrollUserInProduct(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (user.getProducts() == null) {
            user.setProducts(new ArrayList<>());
        }

        user.getProducts().add(product);
        userRepository.save(user);
    }
}
