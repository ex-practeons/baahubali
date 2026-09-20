package com.example.apigateway.ratelimit;

import com.example.apigateway.security.GatewayHeaders;
import java.util.Optional;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Chooses the rate-limit bucket for a request: the authenticated user if there is one, otherwise
 * the client IP.
 *
 * <p>Trusting {@code X-User-Id} here is safe only because {@code JwtAuthenticationGlobalFilter}
 * runs earlier and either overwrites the header from a validated token (protected routes) or
 * strips it (public routes). The route id is part of the key so that routes with different limits
 * never share a bucket: Redis buckets are keyed by this string alone, not by route.
 *
 * <p>Returning an empty Mono makes the gateway deny the request (its default for an empty key).
 */
@Component
public class UserOrIpKeyResolver implements KeyResolver {

    private static final String UNROUTED = "unrouted";

    private final ClientIpResolver clientIpResolver;

    public UserOrIpKeyResolver(ClientIpResolver clientIpResolver) {
        this.clientIpResolver = clientIpResolver;
    }

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String routeId = routeIdOf(exchange);

        return Mono.justOrEmpty(
                userSubject(request)
                        .or(() -> ipSubject(request))
                        .map(subject -> routeId + ":" + subject));
    }

    private Optional<String> userSubject(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(GatewayHeaders.USER_ID))
                .filter(StringUtils::hasText)
                .map(userId -> "user:" + userId);
    }

    private Optional<String> ipSubject(ServerHttpRequest request) {
        return clientIpResolver.resolve(request).map(ip -> "ip:" + ip);
    }

    private static String routeIdOf(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return route != null ? route.getId() : UNROUTED;
    }
}