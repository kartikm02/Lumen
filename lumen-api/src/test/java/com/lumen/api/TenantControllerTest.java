package com.lumen.api;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

class TenantControllerTest {
    private Jwt jwt(Map<String, Object> claims) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "RS256"), claims);
    }
    @Test void readsTenantFromVerifiedPrincipal() {
        assertEquals("tenant-a", new TenantController().tenant(jwt(Map.of("tenant_id", "tenant-a"))).get("tenantId"));
    }
    @Test void rejectsMissingTenant() {
        assertThrows(ResponseStatusException.class, () -> new TenantController().tenant(jwt(Map.of("sub", "user"))));
    }
}
