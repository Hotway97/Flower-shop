package com.example.flowers.ApiTests;

import com.example.flowers.controllers.AdminController;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.models.enums.Role;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTests {

    @Mock private UserService userService;
    @Mock private ProductService productService;
    @Mock private Principal principal;
    @Mock private Model model;
    @Mock private MultipartFile file1, file2, file3;

    @InjectMocks private AdminController adminController;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(); user.setId(1L);
        product = new Product(); product.setId(1L);
    }

    @Test void testAdminPage() {
        when(userService.list()).thenReturn(java.util.List.of(user));
        when(userService.getUserByPrincipal(principal)).thenReturn(user);
        String view = adminController.admin(model, principal);
        assertEquals("admin", view);
        verify(model).addAttribute(eq("users"), any());
        verify(model).addAttribute(eq("user"), any());
    }

    @Test void testUserBan() {
        String view = adminController.userBan(1L);
        verify(userService).banUser(1L);
        assertEquals("redirect:/admin", view);
    }

    @Test void testUserEditForm() {
        when(userService.getUserByPrincipal(principal)).thenReturn(user);
        String view = adminController.userEdit(user, model, principal);
        assertEquals("user-edit", view);
        verify(model).addAttribute("editableUser", user);
        verify(model).addAttribute("currentUser", user);
        verify(model).addAttribute("roles", Role.values());
    }

    @Test void testUserEditRoles() {
        Map<String, String> form = new HashMap<>();
        form.put("ROLE_USER", "on");
        String view = adminController.userEdit(user, form);
        verify(userService).changeUserRoles(user, form);
        assertEquals("redirect:/admin", view);
    }

    @Test void testCreateProduct() throws IOException {
        when(productService.getUserByPrincipal(principal)).thenReturn(user);
        String view = adminController.createProduct(file1, file2, file3, product, principal);
        verify(productService).saveProduct(principal, product, file1, file2, file3);
        assertEquals("redirect:/my/products", view);
    }

    @Test void testDeleteProduct() {
        when(productService.getUserByPrincipal(principal)).thenReturn(user);
        String view = adminController.deleteProduct(1L, principal);
        verify(productService).deleteProduct(user, 1L);
        assertEquals("redirect:/my/products", view);
    }

    @Test void testUserProducts() {
        when(productService.getUserByPrincipal(principal)).thenReturn(user);
        String view = adminController.userProducts(principal, model);
        assertEquals("my-products", view);
        verify(model).addAttribute(eq("user"), eq(user));
        verify(model).addAttribute(eq("products"), any());
    }

    @Test void testProductCreationWithNullFiles() throws IOException {
        String view = adminController.createProduct(null, null, null, product, principal);
        assertEquals("redirect:/my/products", view);
    }

    @Test void testProductDeletionWithoutUser() {
        when(productService.getUserByPrincipal(principal)).thenReturn(null);
        String view = adminController.deleteProduct(99L, principal);
        verify(productService).deleteProduct(null, 99L);
        assertEquals("redirect:/my/products", view);
    }

    @Test void testUserEditFormWithNullPrincipal() {
        when(userService.getUserByPrincipal(null)).thenReturn(null);
        String view = adminController.userEdit(user, model, null);
        assertEquals("user-edit", view);
        verify(model).addAttribute(eq("editableUser"), eq(user));
        verify(model).addAttribute(eq("currentUser"), isNull());
        verify(model).addAttribute(eq("roles"), eq(Role.values()));
    }
}
