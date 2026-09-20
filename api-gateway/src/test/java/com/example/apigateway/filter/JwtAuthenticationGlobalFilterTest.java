package com.example.apigateway.filter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.apigateway.config.GatewaySecurityProperties;
import com.example.apigateway.exception.ExpiredTokenException;
import com.example.apigateway.exception.InvalidTokenException;
import com.example.apigateway.exception.MissingTokenException;
import com.example.apigateway.security.CookieTokenExtractor;
import com.example.apigateway.security.GatewayHeaders;
import com.example.apigateway.security.IdentityHeaderPropagator;
import com.example.apigateway.security.JwtTokenValidator;
import com.example.apigateway.security.PublicRouteMatcher;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class JwtAuthenticationGlobalFilterTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes(UTF_8));

    private final AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
    private final GatewayFilterChain chain = exchange -> {
        forwarded.set(exchange);
        return Mono.empty();
    };

    private final JwtAuthenticationGlobalFilter filter = buildFilter();

    @Test
    void publicRouteIsForwardedWithoutTokenAndSpoofedHeadersAreStripped() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/auth/login")
                        .header(GatewayHeaders.USER_ID, "999")
                        .header(GatewayHeaders.USER_ROLE, "ADMIN")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(forwarded.get()).isNotNull();
        
        org.springframework.http.HttpHeaders headers = forwarded.get().getRequest().getHeaders();
        assertThat(headers.getFirst(GatewayHeaders.USER_ID)).isNull();
        assertThat(headers.getFirst(GatewayHeaders.USER_ROLE)).isNull();
    }

    @Test
    void protectedRouteWithoutCookieIsRejected() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/exams/1").build());

        StepVerifier.create(filter.filter(exchange, chain))
                .expectError(MissingTokenException.class)
                .verify();

        assertThat(forwarded.get()).isNull();
    }

    @Test
    void protectedRouteWithValidCookieForwardsIdentityHeadersAndOverwritesSpoofedOnes() {
        String token = token(Instant.now().plusSeconds(600));
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/exams/1")
                        .cookie(new HttpCookie("jwt_token", token))
                        .header(GatewayHeaders.USER_ID, "999")
                        .header(GatewayHeaders.USER_ROLE, "ADMIN")
                        .build());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(forwarded.get().getRequest().getHeaders().get(GatewayHeaders.USER_ID))
                .containsExactly("42");
        assertThat(forwarded.get().getRequest().getHeaders().get(GatewayHeaders.USER_ROLE))
                .containsExactly("STUDENT");
    }

    @Test
    void expiredTokenIsRejected() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/exams/1")
                        .cookie(new HttpCookie("jwt_token", token(Instant.now().minusSeconds(3600))))
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .expectError(ExpiredTokenException.class)
                .verify();
    }

    @Test
    void tamperedTokenIsRejected() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/exams/1")
                        .cookie(new HttpCookie("jwt_token", "abc.def.ghi"))
                        .build());

        StepVerifier.create(filter.filter(exchange, chain))
                .expectError(InvalidTokenException.class)
                .verify();
    }

    private static String token(Instant expiry) {
        return Jwts.builder()
                .subject("42")
                .claim("role", "STUDENT")
                .expiration(Date.from(expiry))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();
    }

    private static JwtAuthenticationGlobalFilter buildFilter() {
        GatewaySecurityProperties properties = new GatewaySecurityProperties(
                List.of("/api/auth/login", "/api/auth/register"),
                new GatewaySecurityProperties.Jwt(SECRET, "jwt_token", "sub", "role", null, 0));

        return new JwtAuthenticationGlobalFilter(
                new PublicRouteMatcher(properties),
                new CookieTokenExtractor(properties),
                new JwtTokenValidator(properties),
                new IdentityHeaderPropagator());
    }
}