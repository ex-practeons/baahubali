package com.example.apigateway.security;

import java.util.List;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import com.example.apigateway.config.GatewaySecurityProperties;


@Component
public class PublicRouteMatcher {

    private final List<PathPattern> publicPatterns;

    public PublicRouteMatcher(GatewaySecurityProperties properties) {
        PathPatternParser parser = PathPatternParser.defaultInstance;
        this.publicPatterns = properties.publicPaths().stream()
                .map(parser::parse)
                .toList();
    }

    public boolean isPublic(ServerHttpRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }
        PathContainer path = request.getPath().pathWithinApplication();
        return publicPatterns.stream().anyMatch(pattern -> pattern.matches(path));
    }
}