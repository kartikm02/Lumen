package com.lumen.api;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class AudienceValidatorTest {
    private Jwt jwt(Map<String, Object> claims) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "RS256"), claims);
    }
    @Test void acceptsIntendedAudience() {
        assertFalse(SecurityConfig.audienceValidator("lumen").validate(jwt(Map.of("aud", "lumen"))).hasErrors());
    }
    @Test void rejectsOtherAudience() {
        assertTrue(SecurityConfig.audienceValidator("lumen").validate(jwt(Map.of("aud", "other"))).hasErrors());
    }
    @Test void rejectsMissingAudience() {
        assertTrue(SecurityConfig.audienceValidator("lumen").validate(jwt(Map.of("sub", "user"))).hasErrors());
    }
}
