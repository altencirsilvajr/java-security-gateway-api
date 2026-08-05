package com.altencir.securitygateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SecurityGatewayApplication {
  public static void main(String[] args) {
    SpringApplication.run(SecurityGatewayApplication.class, args);
  }
}
