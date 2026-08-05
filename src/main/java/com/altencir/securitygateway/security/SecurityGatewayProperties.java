package com.altencir.securitygateway.security;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security")
public class SecurityGatewayProperties {
  private String issuer = "http://localhost:8080";
  private List<ApiClient> apiClients = new ArrayList<>();
  private Quota quota = new Quota();

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public List<ApiClient> getApiClients() {
    return apiClients;
  }

  public void setApiClients(List<ApiClient> apiClients) {
    this.apiClients = apiClients;
  }

  public Quota getQuota() {
    return quota;
  }

  public void setQuota(Quota quota) {
    this.quota = quota;
  }

  public static class Quota {
    private long limit = 30;
    private Duration window = Duration.ofMinutes(1);

    public long getLimit() {
      return limit;
    }

    public void setLimit(long limit) {
      this.limit = limit;
    }

    public Duration getWindow() {
      return window;
    }

    public void setWindow(Duration window) {
      this.window = window;
    }
  }

  public static class ApiClient {
    private String clientId;
    private String keySha256;

    public String getClientId() {
      return clientId;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public String getKeySha256() {
      return keySha256;
    }

    public void setKeySha256(String keySha256) {
      this.keySha256 = keySha256;
    }
  }
}
