package com.example.flowers.ApiTests;

import com.example.flowers.controllers.OrderController;
import com.example.flowers.dto.OrderDTO;
import com.example.flowers.dto.OrderResponseDTO;
import com.example.flowers.models.*;
import com.example.flowers.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    private User testUser;
    private Product testProduct;
    private Order testOrder;
    private OrderProduct testOrderProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setTitle("Test Product");
        testProduct.setPrice(1000);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus("pending");
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setTotalAmount(1000L);

        testOrderProduct = new OrderProduct();
        testOrderProduct.setId(1L);
        testOrderProduct.setOrder(testOrder);
        testOrderProduct.setProduct(testProduct);
        testOrderProduct.setQuantity(1);

        testOrder.setOrderProducts(List.of(testOrderProduct));
    }

    @Test
    void getAllOrders_WhenOrdersExist_ReturnsOrderList() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getAllOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testOrder.getId(), response.getBody().get(0).getId());
    }

    @Test
    void getOrderById_WhenOrderExists_ReturnsOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        ResponseEntity<OrderResponseDTO> response = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testOrder.getId(), response.getBody().getId());
    }

    @Test
    void getOrdersByUserId_WhenUserHasOrders_ReturnsOrderList() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrdersByUserId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testOrder.getId(), response.getBody().get(0).getId());
    }

    @Test
    void createOrder_WithMissingUserId_ReturnsBadRequest() {
        OrderDTO dto = new OrderDTO();
        dto.setStatus("pending");
        dto.setProductIds(List.of(1L));

        ResponseEntity<?> response = orderController.createOrder(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("user_id is required", response.getBody());
    }

    @Test
    void updateOrder_WithValidData_UpdatesOrder() {
        OrderDTO dto = new OrderDTO();
        dto.setStatus("completed");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        ResponseEntity<OrderResponseDTO> response = orderController.updateOrder(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("completed", response.getBody().getStatus());
    }

    @Test
    void deleteOrder_WithValidId_DeletesOrder() {
        doNothing().when(orderRepository).deleteById(1L);

        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void updateOrderStatus_WithValidData_UpdatesStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        ResponseEntity<OrderResponseDTO> response = orderController.updateOrderStatus(1L, "completed");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("completed", response.getBody().getStatus());
    }

    @Test
    void getOrdersByProductId_WhenProductInOrders_ReturnsOrderList() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrdersByProductId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testOrder.getId(), response.getBody().get(0).getId());
    }
}