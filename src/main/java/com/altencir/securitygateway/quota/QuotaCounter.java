package com.altencir.securitygateway.quota;

import java.time.Duration;

public interface QuotaCounter {
  long increment(String clientId, Duration window);
}
