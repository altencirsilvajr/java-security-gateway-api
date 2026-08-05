package com.altencir.securitygateway.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ProblemResponder {
  public void write(HttpServletRequest request, HttpServletResponse response, int status, String title, String code)
      throws IOException {
    response.setStatus(status);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    var correlationId = String.valueOf(request.getAttribute(CorrelationIdFilter.ATTRIBUTE));
    response.getWriter().write("{\"type\":\"https://httpstatuses.io/" + status
        + "\",\"title\":\"" + title + "\",\"status\":" + status
        + ",\"code\":\"" + code + "\",\"correlationId\":\"" + correlationId + "\"}");
  }
}
