package com.isha.urlshortener.service;

import com.isha.urlshortener.dto.CreateShortUrlRequest;
import com.isha.urlshortener.dto.CreateShortUrlResponse;
import com.isha.urlshortener.dto.UrlInfoResponse;
import com.isha.urlshortener.entity.ShortUrl;
import com.isha.urlshortener.exception.ApiException;
import com.isha.urlshortener.exception.NotFoundException;
import com.isha.urlshortener.repository.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class ShortUrlService {

    private final ShortUrlRepository repository;
    private final String baseUrl;
    private final Random random = new Random();

    public ShortUrlService(ShortUrlRepository repository,
                           @Value("${app.base-url}") String baseUrl) {
        this.repository = repository;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
    }

    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest req) {
        validateUrl(req.getLongUrl());

        String shortCode;
        if (req.getCustomAlias() != null && !req.getCustomAlias().isBlank()) {
            shortCode = req.getCustomAlias();
            if (repository.existsByShortCode(shortCode)) {
                throw new ApiException("custom alias already taken");
            }
        } else {
            shortCode = generateUniqueCode();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = null;
        if (req.getTtlDays() != null && req.getTtlDays() > 0) {
            expires = now.plusDays(req.getTtlDays());
        }

        ShortUrl entity = ShortUrl.builder()
                .longUrl(req.getLongUrl())
                .shortCode(shortCode)
                .createdAt(now)
                .expiresAt(expires)
                .clickCount(0L)
                .build();

        repository.save(entity);

        return new CreateShortUrlResponse(baseUrl + "/" + shortCode, shortCode);
    }

    @Transactional
    public String getLongUrlAndIncrement(String shortCode) {
        ShortUrl s = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("short code not found"));

        if (s.getExpiresAt() != null && LocalDateTime.now().isAfter(s.getExpiresAt())) {
            throw new NotFoundException("link expired");
        }

        s.setClickCount(s.getClickCount() + 1);
        repository.save(s);
        return s.getLongUrl();
    }

    public UrlInfoResponse getInfo(String shortCode) {
        ShortUrl s = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("short code not found"));
        return UrlInfoResponse.builder()
                .longUrl(s.getLongUrl())
                .shortCode(s.getShortCode())
                .clickCount(s.getClickCount())
                .createdAt(s.getCreatedAt())
                .expiresAt(s.getExpiresAt())
                .build();
    }

    public void delete(String shortCode) {
        ShortUrl s = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("short code not found"));
        repository.delete(s);
    }

    private void validateUrl(String url) {
        try {
            URI u = new URI(url);
            if (u.getScheme() == null || (!u.getScheme().equalsIgnoreCase("http") && !u.getScheme().equalsIgnoreCase("https"))) {
                throw new ApiException("Invalid URL scheme");
            }
        } catch (URISyntaxException e) {
            throw new ApiException("Invalid URL");
        }
    }

    private String generateUniqueCode() {
        // generate alphanumeric 7-char code; retry until unique (simple approach)
        for (int i=0;i<10;i++){
            String code = randomAlphaNumeric(7);
            if (!repository.existsByShortCode(code)) return code;
        }
        // fallback to UUID truncated
        return UUID.randomUUID().toString().substring(0,8);
    }

    private String randomAlphaNumeric(int count) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(count);
        for (int i=0;i<count;i++){
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
