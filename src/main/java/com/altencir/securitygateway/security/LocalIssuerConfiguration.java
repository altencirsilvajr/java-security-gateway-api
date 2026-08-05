package com.altencir.securitygateway.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@Profile({"local", "test"})
public class LocalIssuerConfiguration {
  @Bean
  RSAKey localRsaKey() throws Exception {
    var generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    var pair = generator.generateKeyPair();
    return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
        .privateKey((RSAPrivateKey) pair.getPrivate())
        .keyID("ephemeral-local-key")
        .build();
  }

  @Bean
  JwtEncoder jwtEncoder(RSAKey key) {
    return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
  }

  @Bean
  JwtDecoder jwtDecoder(RSAKey key, SecurityGatewayProperties properties) throws Exception {
    var decoder = NimbusJwtDecoder.withPublicKey(key.toRSAPublicKey()).build();
    decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
        JwtValidators.createDefaultWithIssuer(properties.getIssuer())));
    return decoder;
  }
}
