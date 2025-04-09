package com.example.flowers.services;

import com.example.flowers.models.Image;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.repositories.ProductRepository;
import com.example.flowers.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final UserRepository userRepository;

    public List<Product> listProducts(String title, String sortBy, Integer minPrice, Integer maxPrice) {
        // Установка дефолтных значений для цены
        int effectiveMinPrice = minPrice != null ? minPrice : 0;
        int effectiveMaxPrice = maxPrice != null ? maxPrice : Integer.MAX_VALUE;

        if (effectiveMinPrice > effectiveMaxPrice) {
            return Collections.emptyList();
        }

        List<Product> products;

        if (title != null && !title.isEmpty()) {
            // Передаем оригинальный title без преобразований - ILIKE сам обработает регистр
            products = productRepository.findByTitleContainingAndPriceBetween(title,
                    effectiveMinPrice, effectiveMaxPrice);
        } else {
            products = productRepository.findByPriceBetween(effectiveMinPrice, effectiveMaxPrice);
        }

        // Остальная логика сортировки остается без изменений
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "title_asc":
                    return products.stream()
                            .sorted(Product.TitleAscComparator)
                            .collect(Collectors.toList());
                case "title_desc":
                    return products.stream()
                            .sorted(Product.TitleDescComparator)
                            .collect(Collectors.toList());
                case "price_asc":
                    return products.stream()
                            .sorted(Product.PriceAscComparator)
                            .collect(Collectors.toList());
                case "price_desc":
                    return products.stream()
                            .sorted(Product.PriceDescComparator)
                            .collect(Collectors.toList());
                default:
                    return products;
            }
        }
        return products;
    }

    public void saveProduct(Principal principal, Product product, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        Image image1;
        Image image2;
        Image image3;
        if (file1.getSize() != 0) {
            image1 = toImageEntity(file1);
            image1.setPreviewImage(true);
            product.addImageToProduct(image1);
        }
        if (file2.getSize() != 0) {
            image2 = toImageEntity(file2);
            product.addImageToProduct(image2);
        }
        if (file3.getSize() != 0) {
            image3 = toImageEntity(file3);
            product.addImageToProduct(image3);
        }
        log.info("Saving new Product. Title: {}; Author email: {}", product.getTitle(), product.getUser().getEmail());
        Product productFromDb = productRepository.save(product);
        productFromDb.setPreviewImageId(productFromDb.getImages().get(0).getId());
        productRepository.save(product);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userRepository.findByEmail(principal.getName());
    }

    private Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void deleteProduct(User user, Long id) {
        Product product = productRepository.findById(id)
                .orElse(null);
        if (product != null) {
            if (product.getUser().getId().equals(user.getId())) {
                productRepository.delete(product);
                log.info("Product with id = {} was deleted", id);
            } else {
                log.error("User: {} haven't this product with id = {}", user.getEmail(), id);
            }
        } else {
            log.error("Product with id = {} is not found", id);
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
}