package com.example.flowers.ApiTests;

import com.example.flowers.controllers.DeepSeekApiController;
import com.example.flowers.models.Chat;
import com.example.flowers.models.Message;
import com.example.flowers.models.User;
import com.example.flowers.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeepSeekControllerTests {

    @Mock private WebClient deepseekClient;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @Mock @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock private MessageService messageService;

    @InjectMocks
    private DeepSeekApiController deepSeekApiController;

    private User testUser;
    private Chat testChat;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        // Подавляем System.out и System.err в тестах
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));

        // Тестовые данные
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
        testMessage.setContent("Test message");
        testMessage.setAiResponse(false);
        testMessage.setChat(testChat);

        // Моки WebClient
        when(deepseekClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.accept(any(MediaType[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(String.class)).thenReturn(
                Flux.just("{\"choices\":[{\"delta\":{\"content\":\"test\"}}]}")
        );
    }

    @Test
    void shouldSaveUserMessageOnRequest() {
        when(messageService.getMessagesByChat(anyLong())).thenReturn(Collections.singletonList(testMessage));

        Flux<String> response = deepSeekApiController.deepseek(1L, "Test input");

        assertNotNull(response);
        verify(messageService).saveUserMessage(1L, "Test input");
        verify(messageService).saveAiResponse(anyLong(), anyString());
    }

    @Test
    void shouldParseChunkContentCorrectly() {
        String validJsonChunk = "{\"choices\":[{\"delta\":{\"content\":\"test\"}}]}";
        String invalidJsonChunk = "invalid json";

        String validResult = deepSeekApiController.parseChunkContent(validJsonChunk);
        String invalidResult = deepSeekApiController.parseChunkContent(invalidJsonChunk);

        assertEquals("test", validResult);
        assertNull(invalidResult);
    }
}
