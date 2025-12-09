package com.isha.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_urls", indexes = {
        @Index(name = "idx_short_code", columnList = "shortCode")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length = 2048)
    private String longUrl;

    @Column(nullable=false, unique=true, length = 128)
    private String shortCode;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @Column(nullable=false)
    private Long clickCount = 0L;

    @Column(length = 100)
    private String createdBy; // optional: owner info
}
