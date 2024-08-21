package online.talkandtravel.config;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@TestConfiguration
@EnableAutoConfiguration
public class TestContainerConfig {

  @Bean
  @ServiceConnection
  public static PostgreSQLContainer<?> postgreSQLContainer() {
    final PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:15.8-alpine")
            .withUsername("postgres")
            .withPassword("postgres");
    container.start();
    log.info(
        "PostgreSQL container started on port: {}",
        container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT));
    return container;
  }
}
