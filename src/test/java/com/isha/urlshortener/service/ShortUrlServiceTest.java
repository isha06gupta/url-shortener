package com.isha.urlshortener.service;

import com.isha.urlshortener.dto.CreateShortUrlRequest;
import com.isha.urlshortener.entity.ShortUrl;
import com.isha.urlshortener.exception.ApiException;
import com.isha.urlshortener.exception.NotFoundException;
import com.isha.urlshortener.repository.ShortUrlRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class ShortUrlServiceTest {

    @Autowired
    private ShortUrlService service;

    @Autowired
    private ShortUrlRepository repository;

    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createsShortUrl() {
        CreateShortUrlRequest request = request("https://example.com/docs");

        var response = service.createShortUrl(request);

        assertThat(response.getShortCode()).hasSize(7);
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/" + response.getShortCode());
        assertThat(repository.findByShortCode(response.getShortCode()))
                .get()
                .extracting(ShortUrl::getLongUrl, ShortUrl::getClickCount)
                .containsExactly("https://example.com/docs", 0L);
    }

    @Test
    void createsCustomAlias() {
        CreateShortUrlRequest request = request("https://example.com/profile");
        request.setCustomAlias("my-link_123");
        request.setTtlDays(2);

        var response = service.createShortUrl(request);

        ShortUrl saved = repository.findByShortCode("my-link_123").orElseThrow();
        assertThat(response.getShortCode()).isEqualTo("my-link_123");
        assertThat(saved.getExpiresAt()).isAfter(saved.getCreatedAt());
    }

    @Test
    void rejectsAliasCollision() {
        CreateShortUrlRequest first = request("https://example.com/first");
        first.setCustomAlias("taken");
        service.createShortUrl(first);

        CreateShortUrlRequest second = request("https://example.com/second");
        second.setCustomAlias("taken");

        assertThatThrownBy(() -> service.createShortUrl(second))
                .isInstanceOf(ApiException.class)
                .hasMessage("custom alias already taken");
    }

    @Test
    void redirectsAndIncrementsClickCount() {
        CreateShortUrlRequest request = request("https://example.com/target");
        request.setCustomAlias("go");
        service.createShortUrl(request);

        String longUrl = service.getLongUrlAndIncrement("go");

        assertThat(longUrl).isEqualTo("https://example.com/target");
        assertThat(repository.findByShortCode("go")).get()
                .extracting(ShortUrl::getClickCount)
                .isEqualTo(1L);
    }

    @Test
    void rejectsExpiredUrlRedirect() {
        repository.save(ShortUrl.builder()
                .longUrl("https://example.com/old")
                .shortCode("old")
                .createdAt(LocalDateTime.now().minusDays(2))
                .expiresAt(LocalDateTime.now().minusDays(1))
                .clickCount(0L)
                .build());

        assertThatThrownBy(() -> service.getLongUrlAndIncrement("old"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("link expired");
    }

    @Test
    void deletesShortUrl() {
        CreateShortUrlRequest request = request("https://example.com/delete-me");
        request.setCustomAlias("gone");
        service.createShortUrl(request);

        service.delete("gone");

        assertThat(repository.findByShortCode("gone")).isEmpty();
    }

    @Test
    void rejectsInvalidUrlScheme() {
        CreateShortUrlRequest request = request("ftp://example.com/file");

        assertThatThrownBy(() -> service.createShortUrl(request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Invalid URL scheme");
    }

    @Test
    void validatesCustomAliasPattern() {
        CreateShortUrlRequest request = request("https://example.com");
        request.setCustomAlias("bad alias");

        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v -> v.getMessage().equals("Invalid alias pattern"));
    }

    private CreateShortUrlRequest request(String longUrl) {
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setLongUrl(longUrl);
        return request;
    }
}
