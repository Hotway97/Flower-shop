package com.example.flowers.controllers;

import com.example.flowers.models.Message;
import com.example.flowers.services.MessageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
public class OllamaApiController {
    private static final int MAX_HISTORY_MESSAGES = 8;

    private final ChatClient chatClient;
    private final MessageService messageService;

    public OllamaApiController(ChatClient chatClient, MessageService messageService) {
        this.chatClient = chatClient;
        this.messageService = messageService;
    }

    @PostMapping("/ollama")
    public Flux<String> ollama(@RequestParam Long chatId, @RequestParam String input) {
        // 1. Сохраняем сообщение пользователя
        messageService.saveUserMessage(chatId, input);

        // 2. Получаем историю сообщений (только от пользователя)
        List<String> context = messageService.getUserMessagesByChat(chatId).stream()
                .limit(MAX_HISTORY_MESSAGES) // Ограничиваем глубину истории
                .map(Message::getContent)
                .collect(Collectors.toList());

        // 3. Формируем промпт
        String prompt = String.format("""
            Контекст чата:
            %s
            
            Текущее сообщение:
            %s
            
            Ответь только на текущее сообщение, учитывая контекст:""",
                String.join("\n", context),
                input
        );

        // 4. Отправляем запрос и обрабатываем ответ
        AtomicReference<String> fullResponse = new AtomicReference<>("");

        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .doOnNext(chunk -> {
                    // Собираем ответ по частям
                    fullResponse.updateAndGet(current -> current + chunk);
                })
                .doOnComplete(() -> {
                    // 5. Сохраняем полный ответ один раз
                    if (!fullResponse.get().isEmpty()) {
                        messageService.saveAiResponse(chatId, fullResponse.get());
                    }
                });
    }

    @GetMapping("/ollama")
    public Flux<String> ollama(@RequestParam String input) {
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }
}