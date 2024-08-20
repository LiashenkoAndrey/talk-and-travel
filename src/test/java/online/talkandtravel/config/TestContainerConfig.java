package online.talkandtravel.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Test configuration class for setting up Testcontainers with PostgreSQL.
 *
 * <p>Provides a PostgreSQL container configured for integration tests. The container is started and
 * made available for test cases using the {@code @ServiceConnection} annotation.
 */
@TestConfiguration
@EnableAutoConfiguration
public class TestContainerConfig {

  @Bean
  @ServiceConnection
  public static PostgreSQLContainer<?> postgreSQLContainer() {
    final PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:15.8-alpine").withUsername("postgres");
    container.start();
    return container;
  }
}
