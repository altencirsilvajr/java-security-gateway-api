package com.altencir.securitygateway.api;

import com.altencir.securitygateway.security.ApiKeyAuthenticationFilter;
import com.altencir.securitygateway.audit.SecurityAuditEvent;
import com.altencir.securitygateway.audit.SecurityAuditLog;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerJwt")
@SecurityRequirement(name = "apiKey")
public class ProtectedOperationsController {
  private final SecurityAuditLog auditLog;

  public ProtectedOperationsController(SecurityAuditLog auditLog) {
    this.auditLog = auditLog;
  }
  @GetMapping("/api/operations/status")
  OperationStatus operationStatus(@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
    return new OperationStatus(
        "available",
        jwt.getSubject(),
        String.valueOf(request.getAttribute(ApiKeyAuthenticationFilter.CLIENT_ID_ATTRIBUTE)),
        Instant.now());
  }

  @GetMapping("/api/administration/audit")
  List<SecurityAuditEvent> auditStatus() {
    return auditLog.snapshot();
  }

  record OperationStatus(String status, String userId, String clientId, Instant observedAt) {}
}
