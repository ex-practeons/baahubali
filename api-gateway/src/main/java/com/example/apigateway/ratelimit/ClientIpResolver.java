package com.example.apigateway.ratelimit;

import java.util.Optional;
import org.springframework.http.server.reactive.ServerHttpRequest;

public interface ClientIpResolver {

    Optional<String> resolve(ServerHttpRequest request);
}