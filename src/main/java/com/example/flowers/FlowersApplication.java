package com.example.flowers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FlowersApplication {
	public static void main(String[] args) {
		SpringApplication.run(FlowersApplication.class, args);
	}
}
