package com.example.flowers.controllers;

import com.example.flowers.models.User;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/")
    public String products(
            @RequestParam(name = "searchWord", required = false) String searchWord,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "minPrice", required = false) String minPriceStr,
            @RequestParam(name = "maxPrice", required = false) String maxPriceStr,
            Principal principal,
            Model model) {

        // Парсим цены (обработка формата с пробелами)
        Integer minPrice = parsePrice(minPriceStr);
        Integer maxPrice = parsePrice(maxPriceStr);

        model.addAttribute("products", productService.listProducts(searchWord, sortBy, minPrice, maxPrice));
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("minPrice", minPriceStr); // Сохраняем оригинальную строку
        model.addAttribute("maxPrice", maxPriceStr); // Сохраняем оригинальную строку

        return "products";
    }

    private Integer parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return null;
        }
        try {
            // Удаляем все нецифровые символы, кроме минуса
            String normalized = priceStr.replaceAll("[^\\d-]", "");
            return Integer.parseInt(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}