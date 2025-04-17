package com.example.flowers.controllers;

import com.example.flowers.models.Chat;
import com.example.flowers.models.Message;
import com.example.flowers.models.User;
import com.example.flowers.repositories.ChatRepository;
import com.example.flowers.repositories.MessageRepository;
import com.example.flowers.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public MessageController(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChatRepository chatRepository
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<List<Message>> getMessagesByChat(@PathVariable Long chatId) {
        List<Message> messages = messageRepository.findByChatId(chatId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam(required = false) Long userId,
            @RequestParam Long chatId,
            @RequestBody String content,
            @RequestParam(required = false, defaultValue = "false") boolean isAiResponse
    ) {
        if (!isAiResponse && (userId == null || userRepository.findById(userId).isEmpty())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = !isAiResponse ? userRepository.findById(userId).orElse(null) : null;
        Message message = new Message(content, user, chatOpt.get(), isAiResponse);
        return ResponseEntity.ok(messageRepository.save(message));
    }
}