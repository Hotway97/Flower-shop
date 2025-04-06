package com.example.flowers.controllers;

import com.example.flowers.models.Cart;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.services.CartService;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    @GetMapping
    public String showCart(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("cart", user.getCart());
        return "cart";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@AuthenticationPrincipal User user,
                            @PathVariable Long id,
                            Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        try {
            Product product = productService.getProduct(id);
            Cart cart = user.getCart();
            if (cart == null) {
                cart = new Cart();
                cart.setUser(user);
                user.setCart(cart);
                userService.update(user);
            }
            cartService.addProductToCart(cart, product);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Ошибка добавления товара в корзину");
            return "main";
        }
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@AuthenticationPrincipal User user,
                                 @PathVariable Long id,
                                 Model model) {
        try {
            Product product = productService.getProduct(id);
            Cart cart = user.getCart();
            if (cart != null) {
                cartService.removeProductFromCart(cart, product);
            }
            return "redirect:/cart";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Ошибка удаления товара из корзины");
            return "main";
        }
    }

    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = user.getCart();
        if (cart != null) {
            // Здесь можно добавить логику оформления заказа
            //cartService.clearCart(cart);
        }
//        return "redirect:/cart?success";
        return "redirect:/cart";
    }
}