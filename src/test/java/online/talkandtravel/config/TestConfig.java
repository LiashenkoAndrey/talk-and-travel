package online.talkandtravel.config;

import com.redis.testcontainers.RedisContainer;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.util.TestAuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Test configuration class for setting up Testcontainers with PostgreSQL and Redis.
 *
 * <p>Provides a PostgreSQL and Redis containers configured for integration tests. The container is
 * started and
 * made available for test cases using the {@code @ServiceConnection} annotation.
 */
@Slf4j
@TestConfiguration
@EnableAutoConfiguration
public class TestConfig {

  @Bean
  @ServiceConnection(name = "postgres")
  public static PostgreSQLContainer<?> postgreSQLContainer(
      @Value("${POSTGRES_CONTAINER_VERSION}") String containerVersion,
      @Value("${POSTGRES_CONTAINER_USERNAME}") String containerUsername,
      @Value("${POSTGRES_CONTAINER_PASSWORD}") String containerPassword) {

    final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(containerVersion)
            .withUsername(containerUsername)
            .withPassword(containerPassword);

    container.start();

    log.info("PostgreSQL container started on port: {}",
        container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT));
    return container;
  }

  @Bean
  @ServiceConnection(name = "redis")
  public static RedisContainer redisContainer( @Value("${REDIS_CONTAINER_VERSION}")String redisImageName) {
    var redisContainer = new RedisContainer(DockerImageName.parse(redisImageName));

    redisContainer.start();

    log.info("Redis container started on port: {}",
        redisContainer.getMappedPort(RedisContainer.REDIS_PORT));
    return redisContainer;
  }

  @Bean
  public TestAuthenticationService testAuthenticationService() {
    return new TestAuthenticationService();
  }
}
