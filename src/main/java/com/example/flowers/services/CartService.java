package com.example.flowers.services;

import com.example.flowers.models.Cart;
import com.example.flowers.models.CartItem;
import com.example.flowers.models.Product;
import com.example.flowers.repositories.CartItemRepository;
import com.example.flowers.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public void addProductToCart(Cart cart, Product product) {
        // Получаем актуальную корзину с чистым состоянием
        Cart freshCart = cartRepository.findById(cart.getId()).orElseThrow();

        boolean found = false;
        for (CartItem item : freshCart.getCartItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem newItem = new CartItem();
            newItem.setCart(freshCart);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            freshCart.getCartItems().add(newItem);
        }

        freshCart.calculateTotalPrice();
        cartRepository.save(freshCart);
    }

    @Transactional
    public void increaseCartItem(Cart cart, Long cartItemId) {
        CartItem foundItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CartItem not found in current cart"));

        foundItem.setQuantity(foundItem.getQuantity() + 1);
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    @Transactional
    public void decreaseCartItem(Cart cart, Long cartItemId) {
        Optional<CartItem> optionalItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        CartItem item = optionalItem.orElseThrow(() ->
                new IllegalArgumentException("CartItem not found in current cart"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
        }

        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    @Transactional
    public void removeAllCartItem(Cart cart, Long cartItemId) {
        Optional<CartItem> item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst();

        item.ifPresent(cartItem -> {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        });

        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            // Удаляем все CartItem из базы
            for (CartItem item : cart.getCartItems()) {
                cartItemRepository.delete(item);
            }

            // Очищаем список и обнуляем total
            cart.getCartItems().clear();
            cart.setTotalPrice(0);

            // Сохраняем очищенную корзину
            cartRepository.save(cart);
        }
    }
}
