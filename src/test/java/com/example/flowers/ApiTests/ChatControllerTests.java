package com.example.flowers.ApiTests;

import com.example.flowers.controllers.ChatController;
import com.example.flowers.dto.ChatDTO;
import com.example.flowers.models.Chat;
import com.example.flowers.models.Message;
import com.example.flowers.models.User;
import com.example.flowers.repositories.ChatRepository;
import com.example.flowers.repositories.UserRepository;
import com.example.flowers.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTests{

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ChatController chatController;

    private User testUser;
    private Chat testChat;
    private Message testMessage;

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

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setContent("Test message".getBytes(StandardCharsets.UTF_8));
        testMessage.setTimestamp(LocalDateTime.now());
        testMessage.setUser(testUser);
        testMessage.setChat(testChat);
        testMessage.setAiResponse(false);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void getChat_WhenChatExists_ReturnsChatDTO() {
        // Arrange
        testUser.setChat(testChat);
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = chatController.getChat();

        // Assert
        assertTrue(response.getBody() instanceof ChatDTO);
        ChatDTO chatDTO = (ChatDTO) response.getBody();
        assertEquals(testChat.getId(), chatDTO.getId());
        assertEquals(testChat.getChatName(), chatDTO.getChatName());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getChat_WhenNoChat_ReturnsEmptyList() {
        // Arrange
        testUser.setChat(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = chatController.getChat();

        // Assert
        assertEquals(Collections.emptyList(), response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void createChat_WhenNoExistingChat_CreatesNewChat() {
        // Arrange
        testUser.setChat(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(chatRepository.save(any(Chat.class))).thenReturn(testChat);

        // Act
        ResponseEntity<?> response = chatController.createChat();

        // Assert
        assertTrue(response.getBody() instanceof ChatDTO);
        assertEquals(200, response.getStatusCodeValue());
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void createChat_WhenChatExists_ReturnsBadRequest() {
        // Arrange
        testUser.setChat(testChat);
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = chatController.createChat();

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("У вас уже есть чат"));
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void deleteChat_WhenValidOwner_DeletesChat() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));

        // Act
        ResponseEntity<Void> response = chatController.deleteChat(1L);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(chatRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteChat_WhenNotOwner_ReturnsNotFound() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        testChat.setUser(otherUser);

        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));

        // Act
        ResponseEntity<Void> response = chatController.deleteChat(1L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(chatRepository, never()).deleteById(anyLong());
    }

    @Test
    void getChatMessages_WhenValidChat_ReturnsMessages() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(messageService.getMessagesByChat(1L)).thenReturn(Collections.singletonList(testMessage));

        // Act
        ResponseEntity<List<Map<String, Object>>> response = chatController.getChatMessages(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test message", response.getBody().get(0).get("content"));
    }

    @Test
    void getChatMessages_WhenNotOwner_ReturnsForbidden() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        testChat.setUser(otherUser);

        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));

        // Act
        ResponseEntity<List<Map<String, Object>>> response = chatController.getChatMessages(1L);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        verify(messageService, never()).getMessagesByChat(anyLong());
    }

    @Test
    void clearChatHistory_WhenValidChat_ClearsMessages() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));

        // Act
        ResponseEntity<Void> response = chatController.clearChatHistory(1L);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(messageService, times(1)).deleteAllByChatId(1L);
    }

    @Test
    void clearChatHistory_WhenNotOwner_ReturnsForbidden() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        testChat.setUser(otherUser);

        when(userRepository.findByEmail("test@example.com")).thenReturn(testUser);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));

        // Act
        ResponseEntity<Void> response = chatController.clearChatHistory(1L);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        verify(messageService, never()).deleteAllByChatId(anyLong());
    }
}
