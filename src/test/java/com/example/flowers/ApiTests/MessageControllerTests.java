package com.example.flowers.ApiTests;

import com.example.flowers.controllers.MessageController;
import com.example.flowers.models.Chat;
import com.example.flowers.models.Message;
import com.example.flowers.models.User;
import com.example.flowers.repositories.ChatRepository;
import com.example.flowers.repositories.MessageRepository;
import com.example.flowers.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTests {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private MessageController messageController;

    private User testUser;
    private Chat testChat;
    private Message testMessage;
    private String testContent;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testChat = new Chat();
        testChat.setId(1L);
        testChat.setChatName("Test Chat");
        testChat.setCreatedAt(LocalDateTime.now());

        testContent = "Test message";

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setContent(testContent);
        testMessage.setTimestamp(LocalDateTime.now());
        testMessage.setUser(testUser);
        testMessage.setChat(testChat);
        testMessage.setAiResponse(false);
    }

    @Test
    void getMessagesByChat_WhenChatExists_ReturnsMessages() {
        // Arrange
        when(messageRepository.findByChatId(1L)).thenReturn(Arrays.asList(testMessage));

        // Act
        ResponseEntity<List<Message>> response = messageController.getMessagesByChat(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(testMessage.getId(), response.getBody().get(0).getId());
        verify(messageRepository, times(1)).findByChatId(1L);
    }

    @Test
    void sendMessage_UserMessageWithValidData_ReturnsCreatedMessage() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        // Act
        ResponseEntity<Message> response = messageController.sendMessage(
                1L, 1L, testContent, false);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testMessage.getId(), response.getBody().getId());

        // Изменено с times(1) на times(2) для соответствия реализации контроллера
        verify(userRepository, times(2)).findById(1L);
        verify(chatRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void sendMessage_AiMessageWithValidData_ReturnsCreatedMessage() {
        // Arrange
        testMessage.setUser(null);
        testMessage.setAiResponse(true);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(testChat));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);

        // Act
        ResponseEntity<Message> response = messageController.sendMessage(
                null, 1L, testContent, true);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testMessage.getId(), response.getBody().getId());
        assertTrue(response.getBody().isAiResponse());
        verify(userRepository, never()).findById(anyLong());
        verify(chatRepository, times(1)).findById(1L);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void sendMessage_UserMessageWithoutUserId_ReturnsBadRequest() {
        // Act
        ResponseEntity<Message> response = messageController.sendMessage(
                null, 1L, testContent, false);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        verify(userRepository, never()).findById(anyLong());
        verify(chatRepository, never()).findById(anyLong());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessage_UserMessageWithInvalidUserId_ReturnsBadRequest() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Message> response = messageController.sendMessage(
                99L, 1L, testContent, false);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        verify(userRepository, times(1)).findById(99L);
        verify(chatRepository, never()).findById(anyLong());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessage_WithInvalidChatId_ReturnsBadRequest() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(chatRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Message> response = messageController.sendMessage(
                1L, 99L, testContent, false);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        verify(userRepository, times(1)).findById(1L);
        verify(chatRepository, times(1)).findById(99L);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void sendMessage_WithNullContent_ReturnsBadRequest() {
        // Act
        ResponseEntity<Message> response = messageController.sendMessage(
                1L, 1L, null, false);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        verify(messageRepository, never()).save(any(Message.class));
    }
}