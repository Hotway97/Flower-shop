package com.example.flowers.ApiTests;

import com.example.flowers.controllers.ProductController;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTests {

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void products_WithSearchWord_ShouldReturnFilteredProducts() {
        // Arrange
        String searchWord = "rose";
        List<Product> expectedProducts = Arrays.asList(
                createTestProduct(1L, "Red Rose", 200),
                createTestProduct(2L, "White Rose", 250)
        );

        when(productService.listProducts(searchWord, null, null, null))
                .thenReturn(expectedProducts);
        when(userService.getUserByPrincipal(principal)).thenReturn(new User());

        // Act
        String viewName = productController.products(searchWord, null, null, null, principal, model);

        // Assert
        assertEquals("products", viewName);
        verify(model).addAttribute("products", expectedProducts);
        verify(model).addAttribute("searchWord", searchWord);
    }

    @Test
    void products_WithPriceRange_ShouldReturnFilteredProducts() {
        // Arrange
        String minPrice = "100";
        String maxPrice = "300";
        List<Product> expectedProducts = Arrays.asList(
                createTestProduct(1L, "Tulip", 150),
                createTestProduct(2L, "Daisy", 200)
        );

        when(productService.listProducts(null, null, 100, 300))
                .thenReturn(expectedProducts);
        when(userService.getUserByPrincipal(principal)).thenReturn(new User());

        // Act
        String viewName = productController.products(null, null, minPrice, maxPrice, principal, model);

        // Assert
        assertEquals("products", viewName);
        verify(model).addAttribute("products", expectedProducts);
        verify(model).addAttribute("minPrice", minPrice);
        verify(model).addAttribute("maxPrice", maxPrice);
    }

    @Test
    void products_WithSorting_ShouldReturnSortedProducts() {
        // Arrange
        String sortBy = "price_asc";
        List<Product> expectedProducts = Arrays.asList(
                createTestProduct(1L, "Tulip", 100),
                createTestProduct(2L, "Rose", 200)
        );

        when(productService.listProducts(null, sortBy, null, null))
                .thenReturn(expectedProducts);
        when(userService.getUserByPrincipal(principal)).thenReturn(new User());

        // Act
        String viewName = productController.products(null, sortBy, null, null, principal, model);

        // Assert
        assertEquals("products", viewName);
        verify(model).addAttribute("products", expectedProducts);
        verify(model).addAttribute("sortBy", sortBy);
    }

    @Test
    void products_WithInvalidPrice_ShouldHandleGracefully() {
        // Arrange
        when(productService.listProducts(null, null, null, null))
                .thenReturn(Collections.emptyList());
        when(userService.getUserByPrincipal(principal)).thenReturn(new User());

        // Act
        String viewName = productController.products(null, null, "invalid", "price", principal, model);

        // Assert
        assertEquals("products", viewName);
        verify(model).addAttribute("products", Collections.emptyList());
    }

    @Test
    void productInfo_WithExistingProduct_ShouldReturnProductInfoView() {
        // Arrange
        Long productId = 1L;
        Product product = createTestProduct(productId, "Rose", 200);
        User author = new User();
        author.setEmail("author@example.com");
        product.setUser(author);

        when(productService.getProductById(productId)).thenReturn(product);
        when(productService.getUserByPrincipal(principal)).thenReturn(null);

        // Act
        String viewName = productController.productInfo(productId, model, principal);

        // Assert
        assertEquals("product-info", viewName);
        verify(model).addAttribute("user", null);
        verify(model).addAttribute("product", product);
        verify(model).addAttribute("images", product.getImages());
        verify(model).addAttribute("authorProduct", product.getUser());
    }

    @Test
    void productInfo_WithNonExistingProduct_ShouldHandleNull() {
        // Arrange
        Long productId = 999L;

        when(productService.getProductById(productId)).thenReturn(null);
        when(productService.getUserByPrincipal(principal)).thenReturn(null);

        // Act
        String viewName = productController.productInfo(productId, model, principal);

        // Assert
        assertEquals("product-info", viewName);
        verify(model).addAttribute("user", null);
        verify(model).addAttribute("product", null);
    }

    private Product createTestProduct(Long id, String title, Integer price) {
        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setPrice(price);
        product.setUser(new User());
        return product;
    }
}