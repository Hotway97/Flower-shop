package com.example.flowers.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.example.flowers.models.User;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Теперь может быть NULL
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "is_ai_response", nullable = false)
    private boolean isAiResponse = false;

    // Конструкторы
    public Message() {}

    public Message(String content, User user, Chat chat, boolean isAiResponse) {
        this.content = content;
        this.user = user;
        this.chat = chat;
        this.isAiResponse = isAiResponse;
        this.timestamp = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getUser() { return user; }
    public Chat getChat() { return chat; }
    public boolean isAiResponse() { return isAiResponse; }

    public void setId(Long id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setUser(User user) { this.user = user; }
    public void setChat(Chat chat) { this.chat = chat; }
    public void setAiResponse(boolean aiResponse) { isAiResponse = aiResponse; }
}