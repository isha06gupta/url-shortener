package com.isha.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateShortUrlRequest {
    @NotBlank
    private String longUrl;

    // optional custom alias (alphanumeric, dash, underscore)
    @Pattern(regexp = "^[A-Za-z0-9_-]{3,64}$", message = "Invalid alias pattern")
    private String customAlias;

    // optional TTL in days
    private Integer ttlDays;
}
