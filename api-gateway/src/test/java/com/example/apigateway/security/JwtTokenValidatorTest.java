package com.example.apigateway.security;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.apigateway.config.GatewaySecurityProperties;
import com.example.apigateway.exception.ExpiredTokenException;
import com.example.apigateway.exception.InvalidTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtTokenValidatorTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes(UTF_8));
    private static final String OTHER_SECRET = Base64.getEncoder()
            .encodeToString("fedcba9876543210fedcba9876543210".getBytes(UTF_8));

    private final JwtTokenValidator validator = new JwtTokenValidator(properties());

    @Test
    void validTokenYieldsAuthenticatedUser() {
        String token = token(SECRET, "42", "STUDENT", Instant.now().plusSeconds(600));

        AuthenticatedUser user = validator.validate(token);

        assertThat(user.userId()).isEqualTo("42");
        assertThat(user.role()).isEqualTo("STUDENT");
    }

    @Test
    void numericSubjectClaimIsAccepted() {
        String token = Jwts.builder()
                .claim("sub", "42")
                .claim("role", "TEACHER")
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .signWith(key(SECRET))
                .compact();

        assertThat(validator.validate(token).userId()).isEqualTo("42");
    }

    @Test
    void expiredTokenIsRejectedAsExpired() {
        String token = token(SECRET, "42", "STUDENT", Instant.now().minusSeconds(3600));

        assertThatThrownBy(() -> validator.validate(token)).isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    void tokenSignedWithDifferentKeyIsInvalid() {
        String token = token(OTHER_SECRET, "42", "STUDENT", Instant.now().plusSeconds(600));

        assertThatThrownBy(() -> validator.validate(token)).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void malformedTokenIsInvalid() {
        assertThatThrownBy(() -> validator.validate("not.a.jwt")).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void tokenMissingRoleClaimIsInvalid() {
        String token = Jwts.builder()
                .subject("42")
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .signWith(key(SECRET))
                .compact();

        assertThatThrownBy(() -> validator.validate(token)).isInstanceOf(InvalidTokenException.class);
    }

    private static String token(String secret, String userId, String role, Instant expiry) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .expiration(Date.from(expiry))
                .signWith(key(secret))
                .compact();
    }

    private static SecretKey key(String base64Secret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
    }

    private static GatewaySecurityProperties properties() {
        return new GatewaySecurityProperties(
                List.of("/api/auth/login"),
                new GatewaySecurityProperties.Jwt(SECRET, "jwt_token", "sub", "role", null, 0));
    }
}