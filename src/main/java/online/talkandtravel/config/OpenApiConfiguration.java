package online.talkandtravel.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI documentation using Springdoc OpenAPI.
 *
 * <p>This class defines a bean for grouping and configuring OpenAPI documentation endpoints.
 *
 * <p>The {@link GroupedOpenApi} bean is configured to include paths related to the API
 * documentation and the API endpoints:
 *
 * <ul>
 *   <li><strong>group:</strong> The group name for the API documentation. In this case, it is set
 *       to "public-api", which will be used to identify and organize the OpenAPI documentation.
 *   <li><strong>pathsToMatch:</strong> Specifies the paths to include in the OpenAPI documentation.
 *       Here, paths starting with "/swagger-ui/**" and "/api/**" are included. The "/swagger-ui/**"
 *       path is typically used for the Swagger UI documentation interface, while "/api/**" covers
 *       the actual API endpoints.
 * </ul>
 *
 * <p>This configuration allows for grouping and customizing the OpenAPI documentation for different
 * sets of API endpoints.
 *
 */
@Configuration
public class OpenApiConfiguration {
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public-api")
        .pathsToMatch("/swagger-ui/**", "/api/**")
        .build();
  }
}
