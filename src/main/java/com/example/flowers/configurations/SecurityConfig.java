package com.example.flowers.configurations;

import com.example.flowers.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF для статичных ресурсов, чатов, ollama и webhook'ов
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/static/**",
                                "/images/**",
                                "/chats/**",
                                "/ollama",
                                "/payments/webhook",
                                "/api/payments/webhook"
                        )
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Эндпоинты для webhook должны быть доступны без аутентификации
                        .requestMatchers("/payments/webhook", "/api/payments/webhook").permitAll()
                        // Разрешаем доступ для публичных ресурсов
                        .requestMatchers(
                                "/",
                                "/product/**",
                                "/images/**",
                                "/registration",
                                "/login",
                                "/styles/**",
                                "/scripts/**",
                                "/webjars/**",
                                "/static/**"
                        ).permitAll()
                        // Остальные правила (пример)
                        .requestMatchers("/chat").authenticated()
                        .requestMatchers("/cart/**").permitAll()
                        .requestMatchers("/chats/**").authenticated()
                        .requestMatchers("/ollama").permitAll()
                        .requestMatchers("/api/orders/**").permitAll()
                        .requestMatchers("/cart/**").permitAll()
                        // Обратите внимание: правило .requestMatchers("/api/**").permitAll() очень широкое,
                        // убедитесь, что оно соответствует вашим требованиям.
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Объединяем допустимые Origins в одном вызове
        config.setAllowedOrigins(Arrays.asList("http://localhost:8080", "https://flowershop.loca.lt"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
