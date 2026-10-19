package com.atelier.tasks.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService service() {
        JwtService service = new JwtService();
        ReflectionTestUtils.setField(service, "secret", "test-only-secret-with-at-least-32-bytes-long");
        ReflectionTestUtils.setField(service, "expiration", 60_000L);
        return service;
    }

    @Test
    void signedTokenContainsTheExpectedSubject() {
        JwtService service = service();
        assertEquals("owner@example.com", service.subject(service.create("owner@example.com")));
    }

    @Test
    void invalidTokenIsRejected() {
        assertThrows(RuntimeException.class, () -> service().subject("not-a-jwt"));
    }
}
