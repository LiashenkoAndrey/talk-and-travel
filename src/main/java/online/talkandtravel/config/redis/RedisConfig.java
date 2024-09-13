package online.talkandtravel.config.redis;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Configuration class for setting up Redis integration. Enables Redis keyspace notifications for
 * expired keys and configures a listener container to handle key expiration events.
 */
@Configuration
@Log4j2
@EnableRedisRepositories(
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

    private static final String KEY_EVENT_EXPIRED = "__keyevent@*__:expired";

    @Bean
    public RedisTemplate<String, Boolean> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Boolean> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * Configures a {@link RedisMessageListenerContainer} to listen for Redis key expiration events.
     * The listener is triggered when any key expires in the Redis database, and a specific action is
     * performed based on the expired key.
     *
     * @param connectionFactory the Redis connection factory
     * @param expirationListener the listener that handles key expiration events
     * @return the configured {@link RedisMessageListenerContainer}
     */
    @Bean
    RedisMessageListenerContainer keyExpirationListenerContainer(
            RedisConnectionFactory connectionFactory, ExpiredValueRedisListener expirationListener) {
        PatternTopic pattern = new PatternTopic(KEY_EVENT_EXPIRED);
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(connectionFactory);

        // Adds the custom listener to handle key expiration events for the specified pattern
        listenerContainer.addMessageListener(expirationListener, pattern);

        listenerContainer.setErrorHandler(
                e -> log.error("Error in Redis key expiration listener container: {}", e.getMessage()));
        return listenerContainer;
    }
}
