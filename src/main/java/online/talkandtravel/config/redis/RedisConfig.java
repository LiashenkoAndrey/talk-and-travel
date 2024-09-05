package online.talkandtravel.config.redis;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * This class configures a redis database.
 * Enables keyspace events
 */
@Configuration
@Log4j2
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

  private static final String KEY_EVENT_EXPIRED = "__keyevent@*__:expired";

  @Bean
  RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory, ExpiredValueRedisListener expirationListener) {
    PatternTopic pattern = new PatternTopic(KEY_EVENT_EXPIRED);
    RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
    listenerContainer.setConnectionFactory(connectionFactory);
    listenerContainer.addMessageListener(expirationListener, pattern);
    listenerContainer.setErrorHandler(e -> log.error("There was an error in redis key expiration listener container: {}", e.getMessage()));
    return listenerContainer;
  }
}
