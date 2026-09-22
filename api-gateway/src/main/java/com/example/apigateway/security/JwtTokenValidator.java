package com.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.apigateway.config.GatewaySecurityProperties;
import com.example.apigateway.exception.ExpiredTokenException;
import com.example.apigateway.exception.InvalidTokenException;


@Component
public class JwtTokenValidator implements TokenValidator {

    private final JwtParser jwtParser;
    private final String userIdClaim;
    private final String roleClaim;

    public JwtTokenValidator(GatewaySecurityProperties properties) {
        GatewaySecurityProperties.Jwt jwt = properties.jwt();
        this.userIdClaim = jwt.userIdClaim();
        this.roleClaim = jwt.roleClaim();
        this.jwtParser = buildParser(jwt);
    }

    @Override
    public AuthenticatedUser validate(String token) {
        try {
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            return toAuthenticatedUser(claims);
        } catch (ExpiredJwtException ex) {
            throw new ExpiredTokenException(ex);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("JWT could not be verified: " + ex.getMessage(), ex);
        }
    }

    private AuthenticatedUser toAuthenticatedUser(Claims claims) {
        String userId = readScalarClaim(claims, userIdClaim);
        String role = readScalarClaim(claims, roleClaim);

        if (!StringUtils.hasText(userId) || !StringUtils.hasText(role)) {
            throw new InvalidTokenException(
                    "JWT is missing required claims [" + userIdClaim + ", " + roleClaim + "]");
        }
        return new AuthenticatedUser(userId, role);
    }

    /** Accepts string or numeric claims (e.g. a numeric user id); anything else is treated as absent. */
    private static String readScalarClaim(Claims claims, String name) {
        Object value = claims.get(name);
        return (value instanceof String || value instanceof Number) ? value.toString() : null;
    }

    private static JwtParser buildParser(GatewaySecurityProperties.Jwt jwt) {
        SecretKey key = Keys.hmacShaKeyFor(jwt.secret().getBytes(StandardCharsets.UTF_8));

        JwtParserBuilder builder = Jwts.parser()
                .verifyWith(key)
                .clockSkewSeconds(jwt.clockSkewSeconds());

        if (StringUtils.hasText(jwt.issuer())) {
            builder.requireIssuer(jwt.issuer());
        }
        return builder.build();
    }
}