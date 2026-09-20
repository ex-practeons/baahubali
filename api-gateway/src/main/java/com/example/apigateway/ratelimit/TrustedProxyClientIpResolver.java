package com.example.apigateway.ratelimit;

import com.example.apigateway.config.GatewayRateLimitProperties;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Resolves the client IP without trusting spoofable headers.
 *
 * <p>With {@code trustedProxyCount = 0} the TCP peer address is used and {@code X-Forwarded-For}
 * is ignored entirely. With {@code N > 0}, the client address is the Nth entry from the right of
 * {@code X-Forwarded-For}: each trusted proxy appends the address of the peer it received the
 * request from, so entries further left may have been forged by the client and are never used.
 * If the header is shorter than expected, the TCP peer address is used instead.
 */
@Component
public class TrustedProxyClientIpResolver implements ClientIpResolver {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private final int trustedProxyCount;

    public TrustedProxyClientIpResolver(GatewayRateLimitProperties properties) {
        this.trustedProxyCount = properties.trustedProxyCount();
    }

    @Override
    public Optional<String> resolve(ServerHttpRequest request) {
        if (trustedProxyCount > 0) {
            Optional<String> forwarded = fromForwardedFor(request);
            if (forwarded.isPresent()) {
                return forwarded;
            }
        }
        return fromRemoteAddress(request);
    }

    private Optional<String> fromForwardedFor(ServerHttpRequest request) {
        List<String> hops = request.getHeaders().getOrEmpty(X_FORWARDED_FOR).stream()
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();

        int index = hops.size() - trustedProxyCount;
        return index >= 0 ? Optional.of(hops.get(index)) : Optional.empty();
    }

    private Optional<String> fromRemoteAddress(ServerHttpRequest request) {
        return Optional.ofNullable(request.getRemoteAddress())
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress);
    }
}