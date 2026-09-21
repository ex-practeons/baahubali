package com.edtech.iam.security;

import com.edtech.iam.config.JwtProperties;
import com.edtech.iam.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

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

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.expirationMs());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(CLAIM_ROLE, user.getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }
}
