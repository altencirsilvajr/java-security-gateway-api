package com.altencir.securitygateway.api;

import com.altencir.securitygateway.security.ApiKeyAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedOperationsController {
  @GetMapping("/api/operations/status")
  OperationStatus operationStatus(@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
    return new OperationStatus(
        "available",
        jwt.getSubject(),
        String.valueOf(request.getAttribute(ApiKeyAuthenticationFilter.CLIENT_ID_ATTRIBUTE)),
        Instant.now());
  }

  @GetMapping("/api/administration/audit")
  Map<String, String> auditStatus() {
    return Map.of("status", "audit-stream-available");
  }

  record OperationStatus(String status, String userId, String clientId, Instant observedAt) {}
}
