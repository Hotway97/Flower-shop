package com.example.flowers.controllers;

import com.example.flowers.models.Product;
import com.example.flowers.models.User;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Objects;


@Controller
public class CartController {

    private final ProductService productService;
    private final UserService userService;

    public CartController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@AuthenticationPrincipal User user,
                            @PathVariable(name = "id") Long id,
                            Model model
    ) {
        if (user == null){
            return "redirect:/login";
        }
        try {
            Product product = productService.getProduct(id);
            if (!user.getCart().contains(product)) {
                List<Product> cart = user.getCart();
                cart.add(product);
                user.setTotalprice(user.getTotalprice()+ product.getPrice());
                userService.update(user);
            }
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Ошибка!");
            return "main";
        }
    }

    @PostMapping("/cart/remove/{id}")
    public String remove(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        try {
            Product product = productService.getProduct(id);
            List<Product> cart = user.getCart();
            for (Product p: cart){
                if (p.getId() == product.getId()){
                    cart.remove(cart.indexOf(p));
                    break;
                }
            }
            userService.update(user);
            return "redirect:/cart";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Ошибка!");
            return "main";
        }
    }
}