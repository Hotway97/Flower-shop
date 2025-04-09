package com.example.flowers.repositories;

import com.example.flowers.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.title ILIKE %:title%")
    List<Product> findByTitleContaining(@Param("title") String title);


    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetween(@Param("minPrice") Integer minPrice,
                                     @Param("maxPrice") Integer maxPrice);

    @Query("SELECT p FROM Product p WHERE p.title ILIKE %:title% AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByTitleContainingAndPriceBetween(
            @Param("title") String title,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice);

    @Query("SELECT p.price FROM Product p WHERE p.id = :productId")
    Long findPriceById(@Param("productId") Long productId);

    @Query("SELECT p.title FROM Product p WHERE p.id = :productId")
    String findProductTitleById(@Param("productId") Long productId);
}