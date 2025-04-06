package com.example.flowers.services;

import com.example.flowers.models.Order;
import com.example.flowers.models.OrderProduct;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.repositories.OrderRepository;
import com.example.flowers.repositories.ProductRepository;
import com.example.flowers.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setStatus("completed");

        // Получаем id пользователя
        Long userId = order.getUser().getId();

        System.out.println("Дошли до добавления товара пользователем");

        // Для каждого OrderProduct в заказе добавляем товар пользователю
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Long productId = orderProduct.getProduct().getId();
            enrollUserInProduct(userId, productId);
        }

        // Обновляем заказ в БД и возвращаем его
        return orderRepository.save(order);
    }

    @Transactional
    public void enrollUserInProduct(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // Инициализируем коллекцию, если она null
        if (user.getProducts() == null) {
            user.setProducts(new ArrayList<>());
        }

        // Добавляем товар пользователю
        user.getProducts().add(product);
        userRepository.save(user);
    }
}
