package com.example.flowers.services;

import com.example.flowers.models.Cart;
import com.example.flowers.models.Product;
import com.example.flowers.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public void addProductToCart(Cart cart, Product product) {
        if (!cart.getProducts().contains(product)) {
            cart.getProducts().add(product);
            cart.calculateTotalPrice();
            cartRepository.save(cart);
        }
    }

    @Transactional
    public void removeProductFromCart(Cart cart, Product product) {
        cart.getProducts().remove(product);
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getProducts().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);
    }
}