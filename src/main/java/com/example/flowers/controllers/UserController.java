package com.example.flowers.controllers;

import com.example.flowers.models.User;
import com.example.flowers.services.ProductService;
import com.example.flowers.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/login")
    public String login(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "login";
    }

    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с email: " + user.getEmail() + " уже существует");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user,
                          Model model) {
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/info")
    public String info(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "info";
    }

    @GetMapping("/registration")
    public String registration(Principal principal, Model model) {
        model.addAttribute("user", userService.getUserByPrincipal(principal));
        return "registration";
    }

    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable("user") User user,
                           Model model,
                           @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", user);
        model.addAttribute("userByPrincipal", currentUser);
        model.addAttribute("products", user.getProducts());
        return "user-info";
    }
}