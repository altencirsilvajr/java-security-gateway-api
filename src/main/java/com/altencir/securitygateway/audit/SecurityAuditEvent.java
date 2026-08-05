package com.altencir.securitygateway.audit;

import java.time.Instant;

public record SecurityAuditEvent(
    Instant occurredAt,
    String outcome,
    String reason,
    String userId,
    String clientId,
    String method,
    String path,
    String correlationId) {}
