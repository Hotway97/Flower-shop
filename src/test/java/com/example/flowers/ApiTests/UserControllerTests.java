package com.example.flowers.ApiTests;

import com.example.flowers.controllers.UserController;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
    }

    @Test
    void login_WithPrincipal_ShouldReturnLoginView() {
        when(userService.getUserByPrincipal(principal)).thenReturn(testUser);

        String viewName = userController.login(principal, model);

        assertEquals("login", viewName);
        verify(model).addAttribute(eq("user"), eq(testUser));
    }

    @Test
    void login_WithoutPrincipal_ShouldReturnLoginView() {
        when(userService.getUserByPrincipal(eq(null))).thenReturn(new User());

        String viewName = userController.login(null, model);

        assertEquals("login", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void profile_WithAuthenticatedUser_ShouldReturnProfileView() {
        String viewName = userController.profile(testUser, model);

        assertEquals("profile", viewName);
        verify(model).addAttribute(eq("user"), eq(testUser));
    }

    @Test
    void info_WithAuthenticatedUser_ShouldReturnInfoView() {
        String viewName = userController.info(testUser, model);

        assertEquals("info", viewName);
        verify(model).addAttribute(eq("user"), eq(testUser));
    }

    @Test
    void registration_WithPrincipal_ShouldReturnRegistrationView() {
        when(userService.getUserByPrincipal(principal)).thenReturn(testUser);

        String viewName = userController.registration(principal, model);

        assertEquals("registration", viewName);
        verify(model).addAttribute(eq("user"), eq(testUser));
    }

    @Test
    void registration_WithoutPrincipal_ShouldReturnRegistrationView() {
        when(userService.getUserByPrincipal(eq(null))).thenReturn(new User());

        String viewName = userController.registration(null, model);

        assertEquals("registration", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void createUser_WithNewUser_ShouldRedirectToLogin() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        when(userService.createUser(any(User.class))).thenReturn(true);

        String viewName = userController.createUser(newUser, model);

        assertEquals("redirect:/login", viewName);
        verify(model, never()).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void createUser_WithExistingEmail_ShouldReturnRegistrationWithError() {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        when(userService.createUser(any(User.class))).thenReturn(false);

        String viewName = userController.createUser(existingUser, model);

        assertEquals("registration", viewName);
        verify(model).addAttribute(eq("errorMessage"),
                eq("Пользователь с email: " + existingUser.getEmail() + " уже существует"));
    }

    @Test
    void userInfo_WithValidUser_ShouldReturnUserInfoView() {
        User currentUser = new User();
        currentUser.setEmail("current@example.com");
        testUser.setProducts(Collections.emptyList());

        String viewName = userController.userInfo(testUser, model, currentUser);

        assertEquals("user-info", viewName);
        verify(model).addAttribute(eq("user"), eq(testUser));
        verify(model).addAttribute(eq("userByPrincipal"), eq(currentUser));
        verify(model).addAttribute(eq("products"), eq(Collections.emptyList()));
    }

    @Test
    void userInfo_WithNullUser_ShouldHandleNullGracefully() {
        // Arrange
        User currentUser = new User();
        currentUser.setEmail("current@example.com");

        User nullUser = mock(User.class);
        when(nullUser.getProducts()).thenReturn(Collections.emptyList());

        // Act
        String viewName = userController.userInfo(nullUser, model, currentUser);

        // Assert
        assertEquals("user-info", viewName);
        verify(model).addAttribute("user", nullUser);
        verify(model).addAttribute("userByPrincipal", currentUser);
        verify(model).addAttribute("products", Collections.emptyList());
    }
}