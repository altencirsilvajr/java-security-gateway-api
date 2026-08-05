package com.altencir.securitygateway.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ApiClientRegistry {
  private final SecurityGatewayProperties properties;

  public ApiClientRegistry(SecurityGatewayProperties properties) {
    this.properties = properties;
  }

  public Optional<String> authenticate(String candidate) {
    if (candidate == null || candidate.isBlank()) {
      return Optional.empty();
    }
    var candidateDigest = sha256(candidate);
    return properties.getApiClients().stream()
        .filter(client -> client.getClientId() != null && validDigest(client.getKeySha256()))
        .filter(client -> MessageDigest.isEqual(candidateDigest, HexFormat.of().parseHex(client.getKeySha256())))
        .map(SecurityGatewayProperties.ApiClient::getClientId)
        .findFirst();
  }

  private static boolean validDigest(String digest) {
    return digest != null && digest.matches("[0-9a-fA-F]{64}");
  }

  private static byte[] sha256(String value) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 must be available", exception);
    }
  }
}
