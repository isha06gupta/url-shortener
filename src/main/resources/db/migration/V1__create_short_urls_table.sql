CREATE TABLE short_urls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    long_url VARCHAR(2048) NOT NULL,
    short_code VARCHAR(128) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    expires_at TIMESTAMP(6) NULL,
    click_count BIGINT NOT NULL,
    created_by VARCHAR(100) NULL,
    CONSTRAINT uk_short_urls_short_code UNIQUE (short_code)
);

CREATE INDEX idx_short_code ON short_urls (short_code);
