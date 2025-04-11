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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTests {

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private CartController cartController;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        cart = new Cart();
        cart.setId(10L);
        user.setCart(cart);

        product = new Product();
        product.setId(5L);
    }

    // ========== getCartItemCount Tests ==========
    @Test
    void getCartItemCount_WhenUserIsNull_ReturnsZero() {
        ResponseEntity<Map<String, Integer>> response = cartController.getCartItemCount(null);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().get("count"));
    }

    @Test
    void getCartItemCount_WhenUserExists_ReturnsCount() {
        when(cartService.getCartItemsCount(user.getId())).thenReturn(7);

        ResponseEntity<Map<String, Integer>> response = cartController.getCartItemCount(user);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(7, response.getBody().get("count"));
        verify(cartService).getCartItemsCount(user.getId());
    }

    // ========== showCart Tests ==========
    @Test
    void showCart_WhenUserAuthenticated_ReturnsCartViewWithUserAndCart() {
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));

        String viewName = cartController.showCart(user, model);

        assertEquals("cart", viewName);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("cart", cart);
        verify(userService).findById(user.getId());
    }

    @Test
    void showCart_WhenUserNotAuthenticated_RedirectsToLogin() {
        String viewName = cartController.showCart(null, model);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(userService);
        verifyNoInteractions(model);
    }

    // ========== addToCart Tests ==========
    @Test
    void addToCart_WhenProductExists_AddsToCart() {
        when(productService.getProductById(product.getId())).thenReturn(product);

        String viewName = cartController.addToCart(user, product.getId(), null, model);

        assertEquals("redirect:/", viewName);
        verify(cartService).addProductToCart(cart, product);
    }

    @Test
    void addToCart_WhenProductInvalid_ReturnsMainWithErrorMessage() {
        when(productService.getProductById(product.getId())).thenThrow(new IllegalArgumentException());

        String viewName = cartController.addToCart(user, product.getId(), null, model);

        assertEquals("main", viewName);
        verify(model).addAttribute(eq("message"), anyString());
    }

    @Test
    void addToCart_WhenUserNotAuthenticated_RedirectsToLogin() {
        String viewName = cartController.addToCart(null, product.getId(), null, model);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(productService);
        verifyNoInteractions(cartService);
    }

    // ========== increaseCartItem Tests ==========
    @Test
    void increaseCartItem_WhenUserAuthenticated_IncreasesItemQuantity() {
        String viewName = cartController.increaseCartItem(user, product.getId());

        assertEquals("redirect:/cart", viewName);
        verify(cartService).increaseCartItem(cart, product.getId());
    }

    @Test
    void increaseCartItem_WhenUserNotAuthenticated_RedirectsToLogin() {
        String viewName = cartController.increaseCartItem(null, product.getId());

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(cartService);
    }

    // ========== decreaseCartItem Tests ==========
    @Test
    void decreaseCartItem_WhenUserAuthenticated_DecreasesItemQuantity() {
        String viewName = cartController.decreaseCartItem(user, product.getId());

        assertEquals("redirect:/cart", viewName);
        verify(cartService).decreaseCartItem(cart, product.getId());
    }

    @Test
    void decreaseCartItem_WhenUserNotAuthenticated_RedirectsToLogin() {
        String viewName = cartController.decreaseCartItem(null, product.getId());

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(cartService);
    }

    // ========== removeAllCartItem Tests ==========
    @Test
    void removeAllCartItem_WhenUserAndItemExist_RemovesItem() {
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));

        String viewName = cartController.removeAllCartItem(user, product.getId());

        assertEquals("redirect:/cart", viewName);
        verify(cartService).removeAllCartItem(cart, product.getId());
    }

    @Test
    void removeAllCartItem_WhenUserNotFound_ThrowsException() {
        when(userService.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                cartController.removeAllCartItem(user, product.getId())
        );
    }

    // ========== clearCart Tests ==========
    @Test
    void clearCart_WhenCalled_ClearsCartAndReturnsOk() {
        ResponseEntity<Void> response = cartController.clearCart(user.getId());

        assertEquals(200, response.getStatusCodeValue());
        verify(cartService).clearCartByUserId(user.getId());
    }

    // ========== checkout Tests ==========
    @Test
    void checkout_WhenUserAuthenticated_RedirectsToCart() {
        String viewName = cartController.checkout(user);

        assertEquals("redirect:/cart", viewName);
    }

    @Test
    void checkout_WhenUserNotAuthenticated_RedirectsToLogin() {
        String viewName = cartController.checkout(null);

        assertEquals("redirect:/login", viewName);
    }
}
