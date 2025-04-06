package com.example.flowers.repositories;

import com.example.flowers.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitle(String title);
    @Query("SELECT c.price FROM Product c WHERE c.id = :productId")
    Long findPriceById(@Param("productId") Long productId);

    // Получаем только название курса по ID
    @Query("SELECT c.title FROM Product c WHERE c.id = :productId")
    String findProductTitleById(@Param("productId") Long productId);
}
