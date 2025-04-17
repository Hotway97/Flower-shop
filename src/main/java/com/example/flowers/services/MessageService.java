package com.example.flowers.services;

import com.example.flowers.models.Chat;
import com.example.flowers.models.Message;
import com.example.flowers.models.User;
import com.example.flowers.repositories.ChatRepository;
import com.example.flowers.repositories.MessageRepository;
import com.example.flowers.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Transactional
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                          ChatRepository chatRepository,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true) // Для read-only операций
    public List<Message> getUserMessagesByChat(Long chatId) {
        return messageRepository.findByChatIdAndIsAiResponseFalse(chatId);
    }

    @Transactional
    protected User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        return user;
    }

    // Сохранение пользовательского сообщения
    @Transactional
    public Message saveUserMessage(Long chatId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));
        User currentUser = getCurrentUser();

        // Проверяем, является ли пользователь владельцем чата
        if (!chat.getUser().equals(currentUser)) {
            throw new RuntimeException("Доступ запрещен");
        }

        Message message = new Message(
                content, // Передаем строку напрямую
                currentUser,
                chat,
                false
        );
        return messageRepository.save(message);
    }

    // Сохранение ответа ИИ
    @Transactional
    public Message saveAiResponse(Long chatId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));

        Message message = new Message(
                content,
                null,
                chat,
                true
        );
        return messageRepository.save(message);
    }

    // Получение сообщений чата
    @Transactional
    public List<Message> getMessagesByChat(Long chatId) {
        User currentUser = getCurrentUser();
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));

        // Проверяем владельца чата
        if (!chat.getUser().equals(currentUser)) {
            throw new RuntimeException("Доступ запрещен");
        }

        return messageRepository.findByChatIdOrderByTimestampAsc(chatId);
    }

    @Transactional
    public void deleteAllByChatId(Long chatId) {
        messageRepository.deleteAllByChatId(chatId);
    }
}