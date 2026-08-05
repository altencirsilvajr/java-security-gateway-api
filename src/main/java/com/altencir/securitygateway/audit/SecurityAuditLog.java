package com.altencir.securitygateway.audit;

import com.altencir.securitygateway.security.ApiKeyAuthenticationFilter;
import com.altencir.securitygateway.security.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuditLog {
  private static final int CAPACITY = 100;
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAuditLog.class);
  private final ArrayDeque<SecurityAuditEvent> events = new ArrayDeque<>();

  public synchronized void denied(HttpServletRequest request, Authentication authentication, String reason) {
    var event = new SecurityAuditEvent(
        Instant.now(),
        "denied",
        reason,
        authentication == null ? "anonymous" : authentication.getName(),
        attribute(request, ApiKeyAuthenticationFilter.CLIENT_ID_ATTRIBUTE, "unknown"),
        request.getMethod(),
        request.getRequestURI(),
        attribute(request, CorrelationIdFilter.ATTRIBUTE, "unknown"));
    if (events.size() == CAPACITY) {
      events.removeFirst();
    }
    events.addLast(event);
    LOGGER.atWarn()
        .addKeyValue("securityOutcome", event.outcome())
        .addKeyValue("reason", event.reason())
        .addKeyValue("userId", event.userId())
        .addKeyValue("clientId", event.clientId())
        .addKeyValue("method", event.method())
        .addKeyValue("path", event.path())
        .log("security request denied");
  }

  public synchronized List<SecurityAuditEvent> snapshot() {
    return List.copyOf(events);
  }

  public synchronized void clear() {
    events.clear();
  }

  private static String attribute(HttpServletRequest request, String name, String fallback) {
    var value = request.getAttribute(name);
    return value == null ? fallback : String.valueOf(value);
  }
}
