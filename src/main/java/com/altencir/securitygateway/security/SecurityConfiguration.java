package com.altencir.securitygateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ApiKeyAuthenticationFilter apiKeyFilter,
      ProblemResponder problems) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/dev/tokens", "/actuator/health/**", "/v3/api-docs/**", "/swagger-ui/**",
                "/swagger-ui.html")
            .permitAll()
            .requestMatchers("/api/administration/**").hasAuthority("SCOPE_administration.read")
            .requestMatchers("/api/operations/**").hasAuthority("SCOPE_operations.read")
            .anyRequest().authenticated())
        .oauth2ResourceServer(resourceServer -> resourceServer
            .jwt(Customizer.withDefaults())
            .authenticationEntryPoint((request, response, exception) ->
                problems.write(request, response, 401, "Unauthorized", "unauthorized")))
        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint((request, response, exception) ->
                problems.write(request, response, 401, "Unauthorized", "unauthorized"))
            .accessDeniedHandler((request, response, exception) ->
                problems.write(request, response, 403, "Forbidden", "forbidden")))
        .addFilterAfter(apiKeyFilter, BearerTokenAuthenticationFilter.class);
    return http.build();
  }
}
