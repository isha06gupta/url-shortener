package com.isha.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateShortUrlResponse {
    private String shortUrl;
    private String shortCode;
}
