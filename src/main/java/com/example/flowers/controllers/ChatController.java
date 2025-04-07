package com.example.flowers.controllers;

import com.example.flowers.dto.ChatDTO;
import com.example.flowers.models.User;
import com.example.flowers.models.Chat;
import com.example.flowers.models.Message;
import com.example.flowers.repositories.ChatRepository;
import com.example.flowers.repositories.UserRepository;
import com.example.flowers.services.MessageService;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private final ChatRepository chatRepository;
    private final MessageService messageService;
    private final UserRepository userRepository;

    public ChatController(
            ChatRepository chatRepository,
            MessageService messageService,
            UserRepository userRepository
    ) {
        this.chatRepository = chatRepository;
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @Transactional
    protected User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Hibernate.initialize(user.getChat()); // Инициализация чата
        return user;
    }

    // Получение чата текущего пользователя
    @GetMapping
    public ResponseEntity<?> getChat() {
        User currentUser = getCurrentUser();
        Chat chat = currentUser.getChat();
        return chat != null
                ? ResponseEntity.ok(new ChatDTO(chat.getId(), chat.getChatName(), chat.getCreatedAt()))
                : ResponseEntity.ok(Collections.emptyList());
    }

    // Создание чата
    @PostMapping
    public ResponseEntity<?> createChat() {
        User currentUser = getCurrentUser();
        if (currentUser.getChat() != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "У вас уже есть чат"));
        }

        Chat chat = new Chat();
        chat.setUser(currentUser);
        chat.setChatName("Мой чат");
        currentUser.setChat(chat); // Обновляем обратную связь
        chatRepository.save(chat);

        return ResponseEntity.ok(new ChatDTO(chat.getId(), chat.getChatName(), chat.getCreatedAt()));
    }

    // Удаление чата
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Chat> chatOpt = chatRepository.findById(id);

        if (chatOpt.isPresent() && chatOpt.get().getUser().equals(currentUser)) {
            currentUser.setChat(null); // Разрываем связь
            chatRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Получение сообщений чата
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Map<String, Object>>> getChatMessages(@PathVariable Long chatId) {
        try {
            User currentUser = getCurrentUser();
            Optional<Chat> chatOpt = chatRepository.findById(chatId);

            if (!chatOpt.isPresent() || !chatOpt.get().getUser().equals(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            List<Message> messages = messageService.getMessagesByChat(chatId);
            List<Map<String, Object>> response = messages.stream().map(msg -> {
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("id", msg.getId());
                messageData.put("content", new String(msg.getContent(), StandardCharsets.UTF_8));
                messageData.put("isAiResponse", msg.isAiResponse());
                messageData.put("timestamp", msg.getTimestamp());
                messageData.put("userId", msg.getUser() != null ? msg.getUser().getId() : null);
                return messageData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{chatId}/messages")
    public ResponseEntity<Void> clearChatHistory(
            @PathVariable Long chatId
    ) {
        User currentUser = getCurrentUser();
        Optional<Chat> chatOpt = chatRepository.findById(chatId);

        if (chatOpt.isEmpty() || !chatOpt.get().getUser().equals(currentUser)) {
            return ResponseEntity.status(403).build();
        }

        messageService.deleteAllByChatId(chatId);
        return ResponseEntity.noContent().build();
    }
}