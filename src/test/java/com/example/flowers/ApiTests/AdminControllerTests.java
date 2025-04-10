package com.example.flowers.ApiTests;

import com.example.flowers.controllers.AdminController;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.models.enums.Role;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    @Mock
    private MultipartFile file1, file2, file3;

    @InjectMocks
    private AdminController adminController;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>(Collections.singleton(Role.ROLE_ADMIN)));

        product = new Product();
        product.setId(1L);
    }

    // ========== Admin Dashboard Tests ==========
    @Test
    void admin_WhenCalled_ReturnsAdminViewWithUsersAndCurrentUser() {
        when(userService.list()).thenReturn(Collections.singletonList(user));
        when(userService.getUserByPrincipal(principal)).thenReturn(user);

        String viewName = adminController.admin(model, principal);

        assertEquals("admin", viewName);
        verify(model).addAttribute("users", Collections.singletonList(user));
        verify(model).addAttribute("user", user);
    }

    // ========== User Management Tests ==========
    @Test
    void userBan_WhenCalled_BansUserAndRedirects() {
        String viewName = adminController.userBan(user.getId());

        assertEquals("redirect:/admin", viewName);
        verify(userService).banUser(user.getId());
    }

    @Test
    void userEdit_WhenCalled_ReturnsEditFormWithUserData() {
        when(userService.getUserByPrincipal(principal)).thenReturn(user);

        String viewName = adminController.userEdit(user, model, principal);

        assertEquals("user-edit", viewName);
        verify(model).addAttribute("editableUser", user);
        verify(model).addAttribute("currentUser", user);
        verify(model).addAttribute("roles", Role.values());
    }

    @Test
    void userEditRoles_WhenCalled_UpdatesUserRolesAndRedirects() {
        Map<String, String> form = new HashMap<>();
        form.put("ROLE_ADMIN", "on");

        String viewName = adminController.userEdit(user, form);

        assertEquals("redirect:/admin", viewName);
        verify(userService).changeUserRoles(user, form);
    }

    // ========== Product Management Tests ==========
    @Test
    void createProduct_WhenCalled_SavesProductAndRedirects() throws IOException {
        String viewName = adminController.createProduct(file1, file2, file3, product, principal);

        assertEquals("redirect:/my/products", viewName);
        verify(productService).saveProduct(principal, product, file1, file2, file3);
    }

    @Test
    void deleteProduct_WhenCalled_DeletesProductAndRedirects() {
        when(productService.getUserByPrincipal(principal)).thenReturn(user);

        String viewName = adminController.deleteProduct(product.getId(), principal);

        assertEquals("redirect:/my/products", viewName);
        verify(productService).deleteProduct(user, product.getId());
    }

    // ========== My Products Tests ==========
    @Test
    void userProducts_WhenCalled_ReturnsProductsViewWithUserData() {
        when(productService.getUserByPrincipal(principal)).thenReturn(user);
        user.setProducts(Collections.singletonList(product));

        String viewName = adminController.userProducts(principal, model);

        assertEquals("my-products", viewName);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("products", Collections.singletonList(product));
    }

    // Дополнительные edge-case тесты
    @Test
    void userEdit_WithNullPrincipal_HandlesNullGracefully() {
        String viewName = adminController.userEdit(user, model, null);

        assertEquals("user-edit", viewName);
        verify(model).addAttribute("editableUser", user);
        verify(model).addAttribute("currentUser", null);
        verify(model).addAttribute("roles", Role.values());
    }

    @Test
    void createProduct_WithNullFiles_HandlesNullGracefully() throws IOException {
        String viewName = adminController.createProduct(null, null, null, product, principal);

        assertEquals("redirect:/my/products", viewName);
        verify(productService).saveProduct(principal, product, null, null, null);
    }
}