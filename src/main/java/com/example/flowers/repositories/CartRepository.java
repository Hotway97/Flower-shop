package com.example.flowers.repositories;

import com.example.flowers.models.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = {"cartItems"}) // Для загрузки связанных элементов
    Cart findByUserId(Long userId);
}