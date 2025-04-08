package com.example.flowers.controllers;

import com.example.flowers.models.Cart;
import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.services.CartService;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

        // ✅ Загружаем свежего пользователя из базы
        User freshUser = userService.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", freshUser);
        model.addAttribute("cart", freshUser.getCart());
        return "cart";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@AuthenticationPrincipal User user,
                            @PathVariable Long id,
                            @RequestParam(value = "redirectUrl", required = false) String redirectUrl,
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
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                return "redirect:" + redirectUrl;
            }
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Ошибка добавления товара в корзину");
            return "main";
        }
    }


    @PostMapping("/item/increase/{cartItemId}")
    public String increaseCartItem(@AuthenticationPrincipal User user,
                                   @PathVariable Long cartItemId) {
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = user.getCart();
        if (cart != null) {
            cartService.increaseCartItem(cart, cartItemId);
        }
        return "redirect:/cart";
    }

    @PostMapping("/item/decrease/{cartItemId}")
    public String decreaseCartItem(@AuthenticationPrincipal User user,
                                   @PathVariable Long cartItemId) {
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = user.getCart();
        if (cart != null) {
            cartService.decreaseCartItem(cart, cartItemId);
        }
        return "redirect:/cart";
    }

    @PostMapping("/item/removeAll/{cartItemId}")
    public String removeAllCartItem(@AuthenticationPrincipal User user,
                                    @PathVariable Long cartItemId) {
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = user.getCart();
        if (cart != null) {
            cartService.removeAllCartItem(cart, cartItemId);
            // 🔄 Загружаем свежую корзину
            User freshUser = userService.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user.getId()));
            user.setCart(freshUser.getCart());
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = user.getCart();
        if (cart != null) {
            // Здесь может быть логика оформления заказа
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCartByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
