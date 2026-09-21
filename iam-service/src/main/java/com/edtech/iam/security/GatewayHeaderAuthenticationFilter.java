package com.edtech.iam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Trusts identity headers injected by the API Gateway rather than re-verifying the JWT itself.
 *
 * Architecture: the Gateway is the sole JWT validator on the platform. On a successfully
 * validated request it extracts the user's ID and role from the JWT and forwards them as
 * X-User-Id / X-User-Role headers, stripping any such headers a caller tried to spoof.
 * This service is therefore reachable ONLY through the Gateway on the internal network —
 * it must never be exposed directly to the public internet, since this filter performs no
 * cryptographic verification of its own and simply trusts these two headers as given.
 */
@Component
public class GatewayHeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String roleHeader = request.getHeader(USER_ROLE_HEADER);

        if (StringUtils.hasText(userIdHeader) && StringUtils.hasText(roleHeader)) {
            try {
                UUID userId = UUID.fromString(userIdHeader);

                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + roleHeader));

                var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException ex) {
                // Malformed X-User-Id from the Gateway — treat as unauthenticated rather than failing the request.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
