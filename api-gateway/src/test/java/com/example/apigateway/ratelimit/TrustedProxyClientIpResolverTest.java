package com.example.apigateway.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.apigateway.config.GatewayRateLimitProperties;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

class TrustedProxyClientIpResolverTest {

    @Test
    void withoutTrustedProxiesForwardedHeaderIsIgnored() throws Exception {
        var resolver = new TrustedProxyClientIpResolver(new GatewayRateLimitProperties(0));
        var request = MockServerHttpRequest.get("/api/exams/1")
                .remoteAddress(peer("10.0.0.5"))
                .header("X-Forwarded-For", "6.6.6.6")
                .build();

        assertThat(resolver.resolve(request)).contains("10.0.0.5");
    }

    @Test
    void withOneTrustedProxyTheLastHopIsTheClientAndForgedEntriesAreIgnored() throws Exception {
        var resolver = new TrustedProxyClientIpResolver(new GatewayRateLimitProperties(1));
        // The client forged "6.6.6.6"; the trusted load balancer appended the real client address.
        var request = MockServerHttpRequest.get("/api/exams/1")
                .remoteAddress(peer("10.0.0.5"))
                .header("X-Forwarded-For", "6.6.6.6, 203.0.113.7")
                .build();

        assertThat(resolver.resolve(request)).contains("203.0.113.7");
    }

    @Test
    void withTwoTrustedProxiesTheSecondEntryFromTheRightIsTheClient() throws Exception {
        var resolver = new TrustedProxyClientIpResolver(new GatewayRateLimitProperties(2));
        var request = MockServerHttpRequest.get("/api/exams/1")
                .remoteAddress(peer("10.0.0.5"))
                .header("X-Forwarded-For", "203.0.113.7, 10.0.0.9")
                .build();

        assertThat(resolver.resolve(request)).contains("203.0.113.7");
    }

    @Test
    void fallsBackToPeerAddressWhenForwardedHeaderHasTooFewHops() throws Exception {
        var resolver = new TrustedProxyClientIpResolver(new GatewayRateLimitProperties(2));
        var request = MockServerHttpRequest.get("/api/exams/1")
                .remoteAddress(peer("10.0.0.5"))
                .header("X-Forwarded-For", "203.0.113.7")
                .build();

        assertThat(resolver.resolve(request)).contains("10.0.0.5");
    }

    @Test
    void isEmptyWhenNoAddressInformationExists() {
        var resolver = new TrustedProxyClientIpResolver(new GatewayRateLimitProperties(1));
        var request = MockServerHttpRequest.get("/api/exams/1").build();

        assertThat(resolver.resolve(request)).isEmpty();
    }

    private static InetSocketAddress peer(String ip) throws Exception {
        return new InetSocketAddress(InetAddress.getByName(ip), 50000);
    }
}