package com.example.flowers.controllers;

import com.example.flowers.models.Message;
import com.example.flowers.services.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class DeepSeekApiController {

    private final WebClient deepseekClient;
    private final MessageService messageService;

    public DeepSeekApiController(WebClient deepseekClient, MessageService messageService) {
        this.deepseekClient = deepseekClient;
        this.messageService = messageService;
    }

    @PostMapping("/deepseek")
    public Flux<String> deepseek(@RequestParam Long chatId, @RequestParam String input) {
        messageService.saveUserMessage(chatId, input);
        List<Message> messages = messageService.getMessagesByChat(chatId);

        List<Map<String, String>> messageHistory = new ArrayList<>();
        messageHistory.add(Map.of(
                "role", "system",
                "content", """
            Ты — заботливый и вежливый помощник цветочного магазина Flowers shop по имени Букетик.
            === АБСОЛЮТНЫЕ ПРАВИЛА ===
            1. ЯЗЫК: 
               • ТОЛЬКО русский (кириллица) 
               • Запрещено ЛЮБОЕ использование латинских букв (a-z, A-Z)
               • Исключение: цифры (0-9) и знаки препинания (. , ! ? : ; -)
            2. ЗАПРЕЩЕНО:
               • Любые иероглифы (中文, 日本, 한글 등)
               • Спецсимволы (★, ♛, →, ©, ™)
            === ТВОИ ОСНОВНЫЕ ЗАДАЧИ ===
            1. Консультации:
               - Подбор цветочных композиций по индивидуальным запросам
               - Объяснение символики цветов и их сочетаний
               - Советы по уходу за срезанными цветами
            === ПРИМЕРЫ ===
            ДОПУСТИМЫЙ ОТВЕТ:
            Белые розы подчеркнут искренность чувств. Рекомендую дополнить их гипсофилой. 

            НЕДОПУСТИМЫЙ ОТВЕТ:
            White roses + gypsophila — best choice! ★Top solution★
            """
        ));

        for (Message msg : messages) {
            String content = new String(msg.getContent().getBytes(), StandardCharsets.UTF_8);
            messageHistory.add(Map.of(
                    "role", msg.isAiResponse() ? "assistant" : "user",
                    "content", content
            ));
        }

        Map<String, Object> requestBody = Map.of(
                "model", "deepseek-chat",
                "messages", messageHistory,
                "stream", true
        );

        Flux<String> responseStream = deepseekClient.post()
                .bodyValue(requestBody)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .map(String::trim)
                .doOnNext(raw -> System.out.println("Raw chunk: " + raw))
                .filter(chunk -> !chunk.equals("[DONE]"))
                .map(this::parseChunkContent)
                .filter(Objects::nonNull)
                .share();

        ConnectableFlux<String> connectableFlux = responseStream.publish();

        connectableFlux
                .collectList()
                .map(chunks -> String.join("", chunks))
                .doOnSuccess(fullResponse -> {
                    messageService.saveAiResponse(chatId, fullResponse);
                    System.out.println("Сохранили полный ответ: " + fullResponse);
                })
                .subscribe();

        connectableFlux.connect();

        return connectableFlux;
    }


    // Метод для парсинга JSON и извлечения текста
    public String parseChunkContent(String jsonChunk) {
        try {
            JsonNode node = new ObjectMapper().readTree(jsonChunk);
            JsonNode choices = node.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode delta = choices.get(0).path("delta");
                if (delta.has("content")) {
                    return delta.get("content").asText();
                }
            }
            return null;
        } catch (JsonProcessingException e) {
            System.err.println("Ошибка парсинга чанка: " + jsonChunk);
            return null;
        }
    }
}
