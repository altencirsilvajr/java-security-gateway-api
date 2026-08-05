package com.altencir.securitygateway.security;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security")
public class SecurityGatewayProperties {
  private String issuer = "http://localhost:8080";
  private List<ApiClient> apiClients = new ArrayList<>();

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
