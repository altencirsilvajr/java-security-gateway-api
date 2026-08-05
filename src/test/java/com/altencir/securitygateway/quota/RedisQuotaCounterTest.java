package com.altencir.securitygateway.quota;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
class RedisQuotaCounterTest {
  @Container
  private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
      .withExposedPorts(6379);

  @Test
  void incrementIsAtomicAcrossConcurrentCallers() throws Exception {
    var connectionFactory = new LettuceConnectionFactory(REDIS.getHost(), REDIS.getMappedPort(6379));
    connectionFactory.afterPropertiesSet();
    try {
      var counter = new RedisQuotaCounter(new StringRedisTemplate(connectionFactory));
      try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        var tasks = IntStream.range(0, 20)
            .mapToObj(ignored -> (Callable<Long>) () -> counter.increment("concurrent-client", Duration.ofMinutes(1)))
            .toList();
        var observed = executor.invokeAll(tasks).stream().map(future -> {
          try {
            return future.get();
          } catch (Exception exception) {
            throw new AssertionError(exception);
          }
        }).sorted().toList();

        assertThat(observed).containsExactlyElementsOf(
            IntStream.rangeClosed(1, 20).mapToObj(Long::valueOf).toList());
      }
    } finally {
      connectionFactory.destroy();
    }
  }
}
