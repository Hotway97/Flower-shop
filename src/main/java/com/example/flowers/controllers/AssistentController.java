package com.example.flowers.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Controller
public class AssistentController {
    private final ChatClient chatClient;

    public AssistentController(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String index() {
        return "ai"; // Возвращает имя шаблона Thymeleaf
    }
}

@RestController
class OllamaApiController {

    private final ChatClient chatClient;

    public OllamaApiController(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ollama")
    public Flux<String> ollama(@RequestParam String input) {
        return chatClient.prompt()
                .user(input)
                .stream()
                .content();
    }
}