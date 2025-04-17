package com.example.flowers.controllers;

import com.example.flowers.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody Map<String, Object> notification) {
        try {
            String eventType = (String) notification.get("event");
            if (!"payment.succeeded".equals(eventType)) {
                return ResponseEntity.ok().build();
            }

            Map<String, Object> payment = (Map<String, Object>) notification.get("object");
            Map<String, Object> metadata = (Map<String, Object>) payment.get("metadata");

            if (metadata == null || metadata.get("orderId") == null) {
                return ResponseEntity.ok().build();
            }

            try {
                Long orderId = Long.valueOf(metadata.get("orderId").toString());
                orderService.confirmPayment(orderId);
            } catch (NumberFormatException e) {
                return ResponseEntity.ok().build();
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }
}