package com.lumen.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
class TenantController {
    @GetMapping("/v1/tenant")
    Map<String, String> tenant(@AuthenticationPrincipal Jwt jwt) {
        String tenant = jwt.getClaimAsString("tenant_id");
        if (tenant == null || tenant.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing tenant claim");
        }
        return Map.of("tenantId", tenant);
    }
}
