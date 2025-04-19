package com.example.flowers.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Integer price; // цена в копейках

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "product",
            orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    private Long previewImageId;
    private LocalDateTime dateOfCreated;

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.REFRESH,
            fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts = new ArrayList<>();


    @PrePersist
    private void onCreate() {
        dateOfCreated = LocalDateTime.now();
    }

    public void addImageToProduct(Image image) {
        image.setProduct(this);
        images.add(image);
    }

    // Компараторы для сортировки
    public static final Comparator<Product> TitleAscComparator =
            Comparator.comparing(Product::getTitle, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Product> TitleDescComparator =
            (p1, p2) -> p2.getTitle().compareToIgnoreCase(p1.getTitle());

    public static final Comparator<Product> PriceAscComparator =
            Comparator.comparingInt(Product::getPrice);

    public static final Comparator<Product> PriceDescComparator =
            (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());
}