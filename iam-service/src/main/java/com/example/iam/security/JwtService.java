package com.example.iam.security;

import com.example.iam.config.JwtProperties;
import com.example.iam.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

/**
 * Issues JWTs on successful registration/login. Token verification is intentionally NOT
 * performed here — per the platform architecture, the API Gateway is the single point of
 * JWT validation for the whole system. This service only ever needs to mint tokens using
 * the shared JWT_SECRET; the Gateway verifies them using that same secret.
 */
@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;
    private final StringRedisTemplate redisTemplate;

    public JwtService(JwtProperties jwtProperties, StringRedisTemplate redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.expirationMs());
        
        String sessionId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("user:session:" + user.getId(), sessionId, Duration.ofMillis(jwtProperties.expirationMs()));

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(CLAIM_ROLE, user.getRole().name())
                .claim("sessionId", sessionId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }
}
