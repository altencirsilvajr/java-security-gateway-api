package com.altencir.securitygateway.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.altencir.securitygateway.security.SecurityGatewayProperties;

@RestController
@RequestMapping("/api/dev/tokens")
@Profile({"local", "test"})
public class LocalTokenController {
  private final JwtEncoder encoder;
  private final SecurityGatewayProperties properties;

  public LocalTokenController(JwtEncoder encoder, SecurityGatewayProperties properties) {
    this.encoder = encoder;
    this.properties = properties;
  }

  @PostMapping
  TokenResponse issue(@Valid @RequestBody TokenRequest request) {
    var now = Instant.now();
    var administrator = "administrator".equals(request.profile());
    var scopes = administrator ? "operations.read administration.read" : "operations.read";
    var claims = JwtClaimsSet.builder()
        .issuer(properties.getIssuer())
        .subject(request.profile() + "-user")
        .issuedAt(now)
        .expiresAt(now.plus(15, ChronoUnit.MINUTES))
        .claim("scope", scopes)
        .build();
    var header = JwsHeader.with(SignatureAlgorithm.RS256).keyId("ephemeral-local-key").build();
    return new TokenResponse(encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue(), 900);
  }

  record TokenRequest(@Pattern(regexp = "operator|administrator") String profile) {}

  record TokenResponse(String accessToken, int expiresIn) {}
}
