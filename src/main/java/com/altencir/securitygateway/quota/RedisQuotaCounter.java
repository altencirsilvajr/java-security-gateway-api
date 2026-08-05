package com.altencir.securitygateway.quota;

import java.time.Duration;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class RedisQuotaCounter implements QuotaCounter {
  private static final DefaultRedisScript<Long> INCREMENT_WITH_EXPIRY = new DefaultRedisScript<>(
      "local current = redis.call('INCR', KEYS[1]); "
          + "if current == 1 then redis.call('PEXPIRE', KEYS[1], ARGV[1]); end; "
          + "return current;",
      Long.class);
  private final StringRedisTemplate redis;

  public RedisQuotaCounter(StringRedisTemplate redis) {
    this.redis = redis;
  }

  @Override
  public long increment(String clientId, Duration window) {
    var count = redis.execute(
        INCREMENT_WITH_EXPIRY,
        List.of("security-gateway:quota:{" + clientId + "}"),
        String.valueOf(window.toMillis()));
    if (count == null) {
      throw new IllegalStateException("Redis did not return a quota count");
    }
    return count;
  }
}
