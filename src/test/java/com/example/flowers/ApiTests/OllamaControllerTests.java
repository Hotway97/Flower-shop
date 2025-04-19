package com.example.flowers.ApiTests;

import com.example.flowers.models.Chat;
import com.example.flowers.models.User;
import com.example.flowers.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OllamaControllerTests {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private OllamaApiController ollamaApiController;

    @InjectMocks
    private OllamaChatController ollamaChatController;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User testUser;
    private Chat testChat;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testChat = new Chat();
        testChat.setId(1L);
        testChat.setChatName("Test Chat");
        testChat.setCreatedAt(LocalDateTime.now());
        testChat.setUser(testUser);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void chatIndex_WhenUserAuthenticated_AddsUserToModel() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        String viewName = ollamaChatController.index(testUser, model);

        assertEquals("aichat", viewName);
        verify(model).addAttribute("user", testUser);
        verifyNoInteractions(securityContext);
        verifyNoInteractions(authentication);
    }

    // Дополнительные тесты для Chat
    @Test
    void testChatEntity() {
        // Проверка основных свойств Chat
        assertNotNull(testChat);
        assertEquals(1L, testChat.getId());
        assertEquals("Test Chat", testChat.getChatName());
        assertNotNull(testChat.getCreatedAt());
        assertEquals(testUser, testChat.getUser());
    }

    @Test
    void testChatUserRelationship() {
        // Проверка связи Chat с User
        User user = testChat.getUser();
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
    }
}