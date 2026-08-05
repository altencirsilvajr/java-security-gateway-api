package com.altencir.securitygateway.quota;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class InMemoryQuotaCounter implements QuotaCounter {
  private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

  @Override
  public long increment(String clientId, Duration window) {
    return counters.computeIfAbsent(clientId, ignored -> new AtomicLong()).incrementAndGet();
  }

  public void clear() {
    counters.clear();
  }
}
