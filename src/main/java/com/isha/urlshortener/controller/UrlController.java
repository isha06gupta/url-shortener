package com.isha.urlshortener.controller;

import com.isha.urlshortener.dto.CreateShortUrlRequest;
import com.isha.urlshortener.dto.CreateShortUrlResponse;
import com.isha.urlshortener.dto.UrlInfoResponse;
import com.isha.urlshortener.service.ShortUrlService;
import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@RestController
public class UrlController {

    private final ShortUrlService service;

    public UrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<CreateShortUrlResponse> shorten(@Valid @RequestBody CreateShortUrlRequest req) {
        CreateShortUrlResponse res = service.createShortUrl(req);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = service.getLongUrlAndIncrement(shortCode);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(longUrl));
        return ResponseEntity.status(302).headers(headers).build();
    }

    @GetMapping("/api/info/{shortCode}")
    public ResponseEntity<UrlInfoResponse> info(@PathVariable String shortCode) {
        UrlInfoResponse info = service.getInfo(shortCode);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/api/{shortCode}")
    public ResponseEntity<Void> delete(@PathVariable String shortCode) {
        service.delete(shortCode);
        return ResponseEntity.noContent().build();
    }
}
