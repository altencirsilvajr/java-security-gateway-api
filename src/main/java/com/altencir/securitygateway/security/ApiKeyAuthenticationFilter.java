package com.altencir.securitygateway.security;

import com.altencir.securitygateway.audit.SecurityAuditLog;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
  public static final String CLIENT_ID_ATTRIBUTE = ApiKeyAuthenticationFilter.class.getName() + ".clientId";
  private final ApiClientRegistry registry;
  private final ProblemResponder problems;
  private final SecurityAuditLog audit;

  public ApiKeyAuthenticationFilter(ApiClientRegistry registry, ProblemResponder problems, SecurityAuditLog audit) {
    this.registry = registry;
    this.problems = problems;
    this.audit = audit;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    var path = request.getRequestURI();
    return !path.startsWith("/api/operations/") && !path.startsWith("/api/administration/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var clientId = registry.authenticate(request.getHeader("X-Api-Key"));
    if (clientId.isEmpty()) {
      audit.denied(request, org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication(),
          "invalid-api-key");
      problems.write(request, response, 401, "Unauthorized", "unauthorized");
      return;
    }
    request.setAttribute(CLIENT_ID_ATTRIBUTE, clientId.get());
    chain.doFilter(request, response);
  }
}
