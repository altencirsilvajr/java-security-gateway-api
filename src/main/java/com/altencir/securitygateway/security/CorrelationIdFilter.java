package com.altencir.securitygateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {
  public static final String ATTRIBUTE = CorrelationIdFilter.class.getName() + ".id";
  private static final String HEADER = "X-Correlation-Id";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var supplied = request.getHeader(HEADER);
    var correlationId = supplied != null && supplied.matches("[A-Za-z0-9._-]{1,64}")
        ? supplied
        : UUID.randomUUID().toString();
    request.setAttribute(ATTRIBUTE, correlationId);
    response.setHeader(HEADER, correlationId);
    try (var ignored = MDC.putCloseable("correlationId", correlationId)) {
      chain.doFilter(request, response);
    }
  }
}
