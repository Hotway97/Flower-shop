package com.example.flowers.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DeepSeekChatController {

    @GetMapping("/chat")
    public String index() {
        return "aichat";
    }
}
