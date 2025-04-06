package com.example.flowers.services;

import com.example.flowers.models.Cart;
import com.example.flowers.models.CartItem;
import com.example.flowers.models.Product;
import com.example.flowers.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public void addProductToCart(Cart cart, Product product) {
        boolean found = false;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            cart.getCartItems().add(newItem);
        }
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    @Transactional
    public void increaseCartItem(Cart cart, Long cartItemId) {
        for (CartItem item : cart.getCartItems()) {
            if (item.getId().equals(cartItemId)) {
                item.setQuantity(item.getQuantity() + 1);
                cart.calculateTotalPrice();
                cartRepository.save(cart);
                return;
            }
        }
        throw new IllegalArgumentException("CartItem not found");
    }

    @Transactional
    public void decreaseCartItem(Cart cart, Long cartItemId) {
        Iterator<CartItem> iterator = cart.getCartItems().iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getId().equals(cartItemId)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    iterator.remove();
                }
                cart.calculateTotalPrice();
                cartRepository.save(cart);
                return;
            }
        }
        throw new IllegalArgumentException("CartItem not found");
    }

    @Transactional
    public void removeAllCartItem(Cart cart, Long cartItemId) {
        Cart freshCart = cartRepository.findById(cart.getId()).orElse(cart);
        freshCart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        freshCart.calculateTotalPrice();
        cartRepository.save(freshCart);
    }
}
