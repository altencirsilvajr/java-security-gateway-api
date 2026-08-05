package com.altencir.securitygateway.quota;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class QuotaWebConfiguration implements WebMvcConfigurer {
  private final QuotaInterceptor interceptor;

  public QuotaWebConfiguration(QuotaInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(interceptor)
        .addPathPatterns("/api/operations/**", "/api/administration/**");
  }
}
