package com.altencir.securitygateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "security.api-clients[0].client-id=integration-client",
    "security.api-clients[0].key-sha256=7e9eb0710414f05b4adabb53370effedc7498c224e23db3800eec68e9e46879e",
    "security.quota.limit=3"
})
class SecurityFlowTest {
  private final HttpClient client = HttpClient.newHttpClient();

  @LocalServerPort
  private int port;

  @Autowired
  private com.altencir.securitygateway.quota.InMemoryQuotaCounter quotaCounter;

  @Autowired
  private com.altencir.securitygateway.audit.SecurityAuditLog auditLog;

  @BeforeEach
  void resetState() {
    quotaCounter.clear();
    auditLog.clear();
  }

  @Test
  void validJwtAndApiKeyAllowOperationalRequest() throws Exception {
    var response = protectedGet("operator", "/api/operations/status", "integration-api-key");

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).contains("integration-client", "operator-user");
  }

  @Test
  void missingJwtIsUnauthorizedProblemDetails() throws Exception {
    var request = HttpRequest.newBuilder(uri("/api/operations/status"))
        .header("X-Api-Key", "integration-api-key")
        .GET()
        .build();

    var response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(401);
    assertThat(response.headers().firstValue("Content-Type").orElse("")).contains("application/problem+json");
  }

  @Test
  void invalidApiKeyIsUnauthorizedWithoutEchoingCredential() throws Exception {
    var response = protectedGet("operator", "/api/operations/status", "wrong-key");

    assertThat(response.statusCode()).isEqualTo(401);
    assertThat(response.body()).contains("unauthorized").doesNotContain("wrong-key");
    assertThat(auditLog.snapshot()).singleElement().satisfies(event -> {
      assertThat(event.reason()).isEqualTo("invalid-api-key");
      assertThat(event.toString()).doesNotContain("wrong-key");
    });
  }

  @Test
  void operatorWithoutAdministrationScopeIsForbidden() throws Exception {
    var response = protectedGet("operator", "/api/administration/audit", "integration-api-key");

    assertThat(response.statusCode()).isEqualTo(403);
    assertThat(response.body()).contains("forbidden");
  }

  @Test
  void fourthClientRequestInWindowIsRateLimitedAndAudited() throws Exception {
    for (var attempt = 0; attempt < 3; attempt++) {
      assertThat(protectedGet("administrator", "/api/administration/audit", "integration-api-key").statusCode())
          .isEqualTo(200);
    }

    var response = protectedGet("administrator", "/api/administration/audit", "integration-api-key");

    assertThat(response.statusCode()).isEqualTo(429);
    assertThat(response.body()).contains("quota-exceeded");
    assertThat(auditLog.snapshot()).anySatisfy(event -> {
      assertThat(event.reason()).isEqualTo("quota-exceeded");
      assertThat(event.clientId()).isEqualTo("integration-client");
    });
  }

  private HttpResponse<String> protectedGet(String profile, String path, String apiKey)
      throws IOException, InterruptedException {
    var tokenRequest = HttpRequest.newBuilder(uri("/api/dev/tokens"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString("{\"profile\":\"" + profile + "\"}"))
        .build();
    var tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
    assertThat(tokenResponse.statusCode()).isEqualTo(200);
    var token = tokenResponse.body().replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");

    var request = HttpRequest.newBuilder(uri(path))
        .header("Authorization", "Bearer " + token)
        .header("X-Api-Key", apiKey)
        .GET()
        .build();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private URI uri(String path) {
    return URI.create("http://localhost:" + port + path);
  }
}
