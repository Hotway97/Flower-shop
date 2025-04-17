package com.example.flowers.repositories;

import com.example.flowers.models.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId);
    List<Message> findByChatIdOrderByTimestampAsc(Long chatId);
    @Modifying
    @Query("DELETE FROM Message m WHERE m.chat.id = :chatId")
    void deleteAllByChatId(@Param("chatId") Long chatId);
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.isAiResponse = false ORDER BY m.timestamp ASC")
    List<Message> findByChatIdAndIsAiResponseFalse(@Param("chatId") Long chatId);
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.isAiResponse = false ORDER BY m.timestamp DESC")
    List<Message> findUserMessagesByChatId(@Param("chatId") Long chatId, Pageable pageable);
}


