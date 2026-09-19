package com.example.apigateway.security;

import java.util.Optional;
import org.springframework.http.server.reactive.ServerHttpRequest;

public interface TokenExtractor {
    Optional<String> extract(ServerHttpRequest request);
}