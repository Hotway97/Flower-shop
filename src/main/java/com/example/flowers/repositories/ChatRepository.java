package com.example.flowers.repositories;

import com.example.flowers.models.Chat;
import com.example.flowers.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // Новый метод для поиска чата по владельцу
    Optional<Chat> findByUser(User user);
}