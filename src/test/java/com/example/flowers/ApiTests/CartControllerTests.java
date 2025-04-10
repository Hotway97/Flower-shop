package com.example.flowers.ApiTests;

import com.example.flowers.controllers.CartController;
import com.example.flowers.models.Cart;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.services.CartService;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTests {

    @Mock private ProductService productService;
    @Mock private CartService cartService;
    @Mock private UserService userService;
    @Mock private Model model;

    @InjectMocks private CartController cartController;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        cart = new Cart();
        cart.setId(10L);
        user.setCart(cart);
        product = new Product();
        product.setId(5L);
    }

    // --- showCart ---
    @Test
    void showCart_authenticatedUser_returnsCartView() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        String view = cartController.showCart(user, model);
        assertEquals("cart", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("cart", cart);
    }

    @Test
    void showCart_nullUser_redirectsToLogin() {
        String view = cartController.showCart(null, model);
        assertEquals("redirect:/login", view);
    }

    @Test
    void addToCart_invalidProduct_showsError() {
        when(productService.getProductById(5L)).thenThrow(new IllegalArgumentException());
        String view = cartController.addToCart(user, 5L, null, model);
        assertEquals("main", view);
        verify(model).addAttribute(eq("message"), any());
    }

    @Test
    void addToCart_nullUser_redirectsLogin() {
        String view = cartController.addToCart(null, 5L, null, model);
        assertEquals("redirect:/login", view);
    }

    // --- increaseCartItem ---
    @Test
    void increaseCartItem_success() {
        String view = cartController.increaseCartItem(user, 1L);
        assertEquals("redirect:/cart", view);
        verify(cartService).increaseCartItem(cart, 1L);
    }

    @Test
    void increaseCartItem_nullUser_redirectsLogin() {
        String view = cartController.increaseCartItem(null, 1L);
        assertEquals("redirect:/login", view);
    }

    // --- decreaseCartItem ---
    @Test
    void decreaseCartItem_success() {
        String view = cartController.decreaseCartItem(user, 2L);
        assertEquals("redirect:/cart", view);
        verify(cartService).decreaseCartItem(cart, 2L);
    }

    @Test
    void decreaseCartItem_nullUser_redirectsLogin() {
        String view = cartController.decreaseCartItem(null, 2L);
        assertEquals("redirect:/login", view);
    }

    // --- removeAllCartItem ---
    @Test
    void removeAllCartItem_success() {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        String view = cartController.removeAllCartItem(user, 3L);
        assertEquals("redirect:/cart", view);
        verify(cartService).removeAllCartItem(cart, 3L);
    }

    @Test
    void removeAllCartItem_userNotFound_throws() {
        when(userService.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                cartController.removeAllCartItem(user, 3L)
        );
    }

    // --- clearCart ---
    @Test
    void clearCart_success() {
        ResponseEntity<Void> response = cartController.clearCart(1L);
        assertEquals(200, response.getStatusCodeValue());
        verify(cartService).clearCartByUserId(1L);
    }

    // --- checkout ---
    @Test
    void checkout_authenticated_redirectsCart() {
        String view = cartController.checkout(user);
        assertEquals("redirect:/cart", view);
    }

    @Test
    void checkout_nullUser_redirectsLogin() {
        String view = cartController.checkout(null);
        assertEquals("redirect:/login", view);
    }
}
