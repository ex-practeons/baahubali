package com.example.apigateway.ratelimit;

import com.example.apigateway.config.GatewayRateLimitProperties;
import com.example.apigateway.security.GatewayHeaders;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

class UserOrIpKeyResolverTest {

    private final UserOrIpKeyResolver resolver =
            new UserOrIpKeyResolver(new TrustedProxyClientIpResolver(new GatewayRateLimitProperties(0)));

    @Test
    void authenticatedRequestsAreKeyedByRouteAndUser() throws Exception {
        MockServerWebExchange exchange = exchange(
                MockServerHttpRequest.get("/api/exams/1")
                        .remoteAddress(peer("10.0.0.5"))
                        .header(GatewayHeaders.USER_ID, "42")
                        .build(),
                "exam-engine");

        StepVerifier.create(resolver.resolve(exchange))
                .expectNext("exam-engine:user:42")
                .verifyComplete();
    }

    @Test
    void anonymousRequestsAreKeyedByRouteAndClientIp() throws Exception {
        MockServerWebExchange exchange = exchange(
                MockServerHttpRequest.post("/api/auth/login")
                        .remoteAddress(peer("203.0.113.7"))
                        .build(),
                "iam-auth");

        StepVerifier.create(resolver.resolve(exchange))
                .expectNext("iam-auth:ip:203.0.113.7")
                .verifyComplete();
    }

    @Test
    void sameUserOnDifferentRoutesGetsSeparateBuckets() throws Exception {
        var request = MockServerHttpRequest.get("/x")
                .remoteAddress(peer("10.0.0.5"))
                .header(GatewayHeaders.USER_ID, "42")
                .build();

        StepVerifier.create(resolver.resolve(exchange(request, "iam-users")))
                .expectNext("iam-users:user:42")
                .verifyComplete();
        StepVerifier.create(resolver.resolve(exchange(request, "exam-engine")))
                .expectNext("exam-engine:user:42")
                .verifyComplete();
    }

    @Test
    void yieldsNoKeyWhenNeitherUserNorAddressIsAvailable() {
        MockServerWebExchange exchange = exchange(MockServerHttpRequest.get("/x").build(), "exam-engine");

        StepVerifier.create(resolver.resolve(exchange)).verifyComplete();
    }

    private static MockServerWebExchange exchange(MockServerHttpRequest request, String routeId) {
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        Route route = Route.async()
                .id(routeId)
                .uri("http://localhost:8082")
                .predicate(ex -> true)
                .build();
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, route);
        return exchange;
    }

    private static InetSocketAddress peer(String ip) throws Exception {
        return new InetSocketAddress(InetAddress.getByName(ip), 50000);
    }
}