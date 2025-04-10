package com.example.flowers.ApiTests;

import com.example.flowers.controllers.PaymentController;
import com.example.flowers.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTests {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleWebhook_WhenPaymentSucceeded_ShouldConfirmOrder() {
        // Arrange
        Map<String, Object> notification = createValidNotification("payment.succeeded", 1L);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, times(1)).confirmPayment(1L);
    }

    @Test
    void handleWebhook_WhenPaymentWaitingForCapture_ShouldDoNothing() {
        // Arrange
        Map<String, Object> notification = createValidNotification("payment.waiting_for_capture", 1L);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, never()).confirmPayment(anyLong());
    }

    @Test
    void handleWebhook_WhenInvalidEventType_ShouldReturnOkButDoNothing() {
        // Arrange
        Map<String, Object> notification = createValidNotification("invalid.event.type", 1L);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, never()).confirmPayment(anyLong());
    }

    @Test
    void handleWebhook_WhenMissingMetadata_ShouldReturnOkButDoNothing() {
        // Arrange
        Map<String, Object> notification = new HashMap<>();
        notification.put("event", "payment.succeeded");

        Map<String, Object> payment = new HashMap<>();
        payment.put("id", "payment_123");
        payment.put("status", "succeeded");
        payment.put("paid", true);
        // Нет метаданных
        notification.put("object", payment);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, never()).confirmPayment(anyLong());
    }

    @Test
    void handleWebhook_WhenMissingOrderIdInMetadata_ShouldReturnOkButDoNothing() {
        // Arrange
        Map<String, Object> notification = new HashMap<>();
        notification.put("event", "payment.succeeded");

        Map<String, Object> payment = new HashMap<>();
        payment.put("id", "payment_123");
        payment.put("status", "succeeded");
        payment.put("paid", true);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("otherField", "value"); // Нет orderId
        payment.put("metadata", metadata);

        notification.put("object", payment);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, never()).confirmPayment(anyLong());
    }

    @Test
    void handleWebhook_WhenServiceThrowsException_ShouldReturnOk() {
        // Arrange
        Map<String, Object> notification = createValidNotification("payment.succeeded", 1L);
        doThrow(new RuntimeException("Service error")).when(orderService).confirmPayment(1L);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, times(1)).confirmPayment(1L);
    }

    @Test
    void handleWebhook_WhenMetadataIsNull_ShouldReturnOkButDoNothing() {
        // Arrange
        Map<String, Object> notification = new HashMap<>();
        notification.put("event", "payment.succeeded");

        Map<String, Object> payment = new HashMap<>();
        payment.put("id", "payment_123");
        payment.put("status", "succeeded");
        payment.put("paid", true);
        payment.put("metadata", null); // Явный null
        notification.put("object", payment);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, never()).confirmPayment(anyLong());
    }

    @Test
    void handleWebhook_WhenOrderIdIsNotNumber_ShouldReturnOkButDoNothing() {
        // Arrange
        Map<String, Object> notification = new HashMap<>();
        notification.put("event", "payment.succeeded");

        Map<String, Object> payment = new HashMap<>();
        payment.put("id", "payment_123");
        payment.put("status", "succeeded");
        payment.put("paid", true);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", "not_a_number"); // Не число
        payment.put("metadata", metadata);

        notification.put("object", payment);

        // Act
        ResponseEntity<?> response = paymentController.handleWebhook(notification);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(orderService, never()).confirmPayment(anyLong());
    }

    private Map<String, Object> createValidNotification(String eventType, Long orderId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("event", eventType);

        Map<String, Object> payment = new HashMap<>();
        payment.put("id", "payment_123");
        payment.put("status", "succeeded");
        payment.put("paid", true);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("orderId", orderId);
        payment.put("metadata", metadata);

        notification.put("object", payment);
        return notification;
    }
}