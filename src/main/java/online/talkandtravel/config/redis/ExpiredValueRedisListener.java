package online.talkandtravel.config.redis;

import static online.talkandtravel.util.RedisUtils.getUserIdFromRedisKey;
import static online.talkandtravel.util.constants.ApiPathConstants.USERS_ONLINE_STATUS_ENDPOINT;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.service.OnlineService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * This class handles events triggered when a key expires. Upon expiration, it publishes an event
 * indicating that the user's online status has been updated to {@code UserOnlineStatus.OFFLINE}.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ExpiredValueRedisListener implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineService onlineService;
    private static final String IS_ONLINE_FIELD = "isOnline";

    /**
     * Invoked when a Redis key expires. This method processes the expired key
     * and updates the user's online status to OFFLINE by publishing an event.
     *
     * @param message the message containing the expired key details
     * @param pattern the pattern used for key expiration events (not used in this method)
     */
    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        log.info("Expired key: {}, pattern: {}", new String(message.getBody()), new String(pattern));
        if (isOnlineKey(message)) {
            updateLastSeenOn(message);
        }
    }

    private boolean isOnlineKey(Message message) {
        String key = new String(message.getBody());
        return key.contains(IS_ONLINE_FIELD);
    }

    private void updateLastSeenOn(Message message) {
        try {
            String key = new String(message.getBody());
            Long userId = getUserIdFromRedisKey(key);
            ZonedDateTime lastSeenOn = ZonedDateTime.now(ZoneOffset.UTC);

            onlineService.updateLastSeenOn(userId, lastSeenOn);
            notifyAllUserIsOffline(userId, lastSeenOn);
        } catch (Exception e) {
            log.error("onMessage:{}", e.getMessage());
        }
    }

    private void notifyAllUserIsOffline(Long userId, ZonedDateTime lastSeenOn) {
        OnlineStatusDto statusDto = new OnlineStatusDto(userId, false, lastSeenOn);
        messagingTemplate.convertAndSend(USERS_ONLINE_STATUS_ENDPOINT, statusDto);
    }
}
