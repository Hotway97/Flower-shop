package com.example.flowers.controllers;

import com.example.flowers.models.Message;
import com.example.flowers.services.MessageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OllamaApiController {

    private final ChatClient chatClient;
    private final MessageService messageService;

    public OllamaApiController(ChatClient chatClient, MessageService messageService) {
        this.chatClient = chatClient;
        this.messageService = messageService;
    }

    @PostMapping("/ollama")
    public Flux<String> ollama(@RequestParam Long chatId, @RequestParam String input) {
        // Сохранение пользовательского сообщения
        messageService.saveUserMessage(chatId, input);

        List<Message> messages = messageService.getMessagesByChat(chatId);
        List<String> formattedHistory = new ArrayList<>();

        // Если есть история сообщений, обрабатываем ее
        if (!messages.isEmpty()) {
            formattedHistory = messages.stream()
                    .map(msg -> {
                        String content = new String(msg.getContent(), StandardCharsets.UTF_8);
                        return msg.isAiResponse()
                                ? "Ответ: " + content
                                : "Сообщение пользователя: " + content;
                    })
                    .collect(Collectors.toList());
        }


        // Создаём поток ответа от AI
        Flux<String> responseStream = chatClient.prompt()
                .user(input)
                //.user(String.join("\n", formattedHistory))
                .stream()
                .content()
                .share();



        // Сохранение полного ответа в бд
        responseStream.collectList()
                .map(responseList -> String.join("", responseList))
                .doOnSuccess(fullResponse -> messageService.saveAiResponse(chatId, fullResponse))
                .subscribe();

        return responseStream;
    }

    @GetMapping("/ollama")
    public Flux<String> ollama(@RequestParam String input) {
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }
}
