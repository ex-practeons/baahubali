package com.example.apigateway.config;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Gateway-level rate limiting settings that are not part of a route's own limiter arguments.
 * The per-route limits (rate, burst, cost) live with each route in application.yml.
 *
 * @param trustedProxyCount how many trusted reverse proxies sit in front of the gateway.
 *                          0 means the direct TCP peer address is used and X-Forwarded-For is ignored.
 */
@Validated
@ConfigurationProperties(prefix = "gateway.rate-limit")
public record GatewayRateLimitProperties(
        @DefaultValue("0") @PositiveOrZero int trustedProxyCount) {
}