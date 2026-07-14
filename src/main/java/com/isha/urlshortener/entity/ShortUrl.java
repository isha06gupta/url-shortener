package com.isha.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_urls", indexes = {
        @Index(name = "idx_short_code", columnList = "short_code")
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

    @Column(name = "long_url", nullable=false, length = 2048)
    private String longUrl;

    @Column(name = "short_code", nullable=false, unique=true, length = 128)
    private String shortCode;

    @Column(name = "created_at", nullable=false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "click_count", nullable=false)
    private Long clickCount = 0L;

    @Column(name = "created_by", length = 100)
    private String createdBy; // optional: owner info
}
