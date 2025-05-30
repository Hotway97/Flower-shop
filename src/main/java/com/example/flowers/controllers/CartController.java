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

import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartItemCount(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.ok(Collections.singletonMap("count", 0));
        }

        int count = cartService.getCartItemsCount(user.getId());
        return ResponseEntity.ok(Collections.singletonMap("count", count));
    }

    @GetMapping
    public String showCart(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }

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
            Product product = productService.getProductById(id);
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
            User freshUser = userService.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user.getId()));
            user.setCart(freshUser.getCart());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCartByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
