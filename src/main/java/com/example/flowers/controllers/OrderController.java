package com.example.flowers.controllers;

import com.example.flowers.dto.OrderDTO;
import com.example.flowers.dto.OrderResponseDTO;
import com.example.flowers.models.Order;
import com.example.flowers.models.OrderProduct;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.repositories.OrderRepository;
import com.example.flowers.repositories.ProductRepository;
import com.example.flowers.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Value("${SHOP_ID}")
    private String shopId;

    @Value("${SHOP_API_KEY}")
    private String secretKey;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Получить все заказы
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderRepository.findAll().stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // Получить заказ по ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return ResponseEntity.ok(new OrderResponseDTO(order));
    }

    // Получить заказы пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponseDTO> userOrders = orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userOrders);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO dto) {
        Order order = new Order();
        order.setStatus(dto.getStatus());

        if (dto.getUserId() == null) {
            return ResponseEntity.badRequest().body("user_id is required");
        }
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + dto.getUserId()));
        order.setUser(user);

        List<Long> productIds = dto.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            throw new RuntimeException("Не предоставлены товары для заказа");
        }

        Map<Long, Long> productCountMap = productIds.stream()
                .collect(Collectors.groupingBy(pid -> pid, Collectors.counting()));

        List<OrderProduct> orderProducts = new ArrayList<>();
        long totalAmount = 0;
        List<String> productNames = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : productCountMap.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue().intValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Продукт не найден с id: " + productId));

            Long price = productRepository.findPriceById(productId);
            if (price == null || price <= 0) {
                throw new RuntimeException("Некорректная цена для продукта с id: " + productId);
            }
            totalAmount += price * quantity;

            String productTitle = productRepository.findProductTitleById(productId);
            productNames.add(productTitle);

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(product);
            orderProduct.setQuantity(quantity);
            orderProduct.setOrder(order);
            orderProducts.add(orderProduct);
        }

        order.setOrderProducts(orderProducts);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        String description = "Оплата товаров: " + String.join(", ", productNames);
        String paymentUrl = createYooKassaPayment(order.getId(), totalAmount, description);

        return ResponseEntity.ok(Map.of(
                "orderId", order.getId(),
                "paymentUrl", paymentUrl
        ));
    }

    private String createYooKassaPayment(Long orderId, Long amount, String description) {
        try {
            String auth = shopId + ":" + secretKey;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            String idempotenceKey = UUID.randomUUID().toString();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("Idempotence-Key", idempotenceKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String amountForYooKassa = amount + ".00";

            Map<String, Object> request = new HashMap<>();
            request.put("amount", Map.of(
                    "value", amountForYooKassa,
                    "currency", "RUB"
            ));
            request.put("description", description);
            request.put("confirmation", Map.of(
                    "type", "redirect",
                    "return_url", "https://flowershop.loca.lt/profile"
            ));
            request.put("capture", true);
            request.put("metadata", Map.of(
                    "orderId", orderId
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.yookassa.ru/v3/payments",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map confirmation = (Map) response.getBody().get("confirmation");
                return (String) confirmation.get("confirmation_url");
            } else {
                throw new RuntimeException("Ошибка при создании платежа: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при работе с ЮКассой: " + e.getMessage(), e);
        }
    }

    // Обновить заказ (при необходимости можно добавить логику обновления позиций)
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO dto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        existingOrder.setStatus(dto.getStatus());
        Order updatedOrder = orderRepository.save(existingOrder);
        return ResponseEntity.ok(new OrderResponseDTO(updatedOrder));
    }

    // Удалить заказ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Обновить статус заказа
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(new OrderResponseDTO(updatedOrder));
    }

    // Получить заказы, содержащие конкретный продукт
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByProductId(@PathVariable Long productId) {
        List<OrderResponseDTO> productOrders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderProducts().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(productId)))
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productOrders);
    }
}
