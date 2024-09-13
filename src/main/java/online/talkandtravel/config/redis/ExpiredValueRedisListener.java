package online.talkandtravel.config.redis;

import static online.talkandtravel.util.RedisUtils.getUserIdFromRedisKey;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.OnlineService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * This class handles events triggered when a key expires. Upon expiration, it publishes an event
 * indicating that the user's online status has been updated to {@code UserOnlineStatus.OFFLINE}.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ExpiredValueRedisListener implements MessageListener {

    private final OnlineService onlineService;

    /**
     * Invoked when a Redis key expires. This method processes the expired key
     * and updates the user's online status to OFFLINE by publishing an event.
     *
     * @param message the message containing the expired key details
     * @param pattern the pattern used for key expiration events (not used in this method)
     */
    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            String key = new String(message.getBody());
            Long userId = getUserIdFromRedisKey(key);
            onlineService.notifyUserOnlineStatusUpdated(userId, false);
        } catch (Exception e) {
            log.error("onMessage:{}", e.getMessage());
        }
    }

}
