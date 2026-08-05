package com.altencir.securitygateway.quota;

import com.altencir.securitygateway.audit.SecurityAuditLog;
import com.altencir.securitygateway.security.ApiKeyAuthenticationFilter;
import com.altencir.securitygateway.security.ProblemResponder;
import com.altencir.securitygateway.security.SecurityGatewayProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class QuotaInterceptor implements HandlerInterceptor {
  private final QuotaCounter counter;
  private final SecurityGatewayProperties properties;
  private final SecurityAuditLog audit;
  private final ProblemResponder problems;

  public QuotaInterceptor(
      QuotaCounter counter,
      SecurityGatewayProperties properties,
      SecurityAuditLog audit,
      ProblemResponder problems) {
    this.counter = counter;
    this.properties = properties;
    this.audit = audit;
    this.problems = problems;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    var clientId = String.valueOf(request.getAttribute(ApiKeyAuthenticationFilter.CLIENT_ID_ATTRIBUTE));
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    try {
      var count = counter.increment(clientId, properties.getQuota().getWindow());
      if (count <= properties.getQuota().getLimit()) {
        return true;
      }
      response.setHeader("Retry-After", String.valueOf(properties.getQuota().getWindow().toSeconds()));
      audit.denied(request, authentication, "quota-exceeded");
      problems.write(request, response, 429, "Too Many Requests", "quota-exceeded");
      return false;
    } catch (RuntimeException exception) {
      audit.denied(request, authentication, "quota-unavailable");
      problems.write(request, response, 503, "Service Unavailable", "quota-unavailable");
      return false;
    }
  }
}
