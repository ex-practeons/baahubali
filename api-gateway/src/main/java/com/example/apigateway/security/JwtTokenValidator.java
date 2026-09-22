package com.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.example.apigateway.config.GatewaySecurityProperties;
import com.example.apigateway.exception.ExpiredTokenException;
import com.example.apigateway.exception.InvalidTokenException;
import reactor.core.publisher.Mono;

@Component
public class JwtTokenValidator implements TokenValidator {

    private final JwtParser jwtParser;
    private final String userIdClaim;
    private final String roleClaim;
    private final ReactiveStringRedisTemplate redisTemplate;

    public JwtTokenValidator(GatewaySecurityProperties properties, ReactiveStringRedisTemplate redisTemplate) {
        GatewaySecurityProperties.Jwt jwt = properties.jwt();
        this.userIdClaim = jwt.userIdClaim();
        this.roleClaim = jwt.roleClaim();
        this.redisTemplate = redisTemplate;
        this.jwtParser = buildParser(jwt);
    }

    @Override
    public Mono<AuthenticatedUser> validate(String token) {
        return Mono.fromCallable(() -> {
            try {
                return jwtParser.parseSignedClaims(token).getPayload();
            } catch (ExpiredJwtException ex) {
                throw new ExpiredTokenException(ex);
            } catch (JwtException | IllegalArgumentException ex) {
                throw new InvalidTokenException("JWT could not be verified: " + ex.getMessage(), ex);
            }
        }).flatMap(claims -> {
            String userId = readScalarClaim(claims, userIdClaim);
            String role = readScalarClaim(claims, roleClaim);
            String sessionId = readScalarClaim(claims, "sessionId");

            if (!StringUtils.hasText(userId) || !StringUtils.hasText(role) || !StringUtils.hasText(sessionId)) {
                return Mono.error(new InvalidTokenException(
                        "JWT is missing required claims [" + userIdClaim + ", " + roleClaim + ", sessionId]"));
            }

            // Enforce Single Login: Verify the session in the JWT matches the active session in Redis
            return redisTemplate.opsForValue().get("user:session:" + userId)
                    .switchIfEmpty(Mono.error(new InvalidTokenException("Session expired or user logged out.")))
                    .flatMap(activeSessionId -> {
                        if (!activeSessionId.equals(sessionId)) {
                            return Mono.error(new InvalidTokenException("Logged in from another device. Session invalidated."));
                        }
                        return Mono.just(new AuthenticatedUser(userId, role));
                    });
        });
    }

    private static String readScalarClaim(Claims claims, String name) {
        Object value = claims.get(name);
        return (value instanceof String || value instanceof Number) ? value.toString() : null;
    }

    private static JwtParser buildParser(GatewaySecurityProperties.Jwt jwt) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwt.secret()));
        JwtParserBuilder builder = Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(jwt.clockSkewSeconds());
        if (StringUtils.hasText(jwt.issuer())) {
            builder.requireIssuer(jwt.issuer());
        }
        return builder.build();
    }
}