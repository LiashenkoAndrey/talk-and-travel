package online.talkandtravel.config.redis;

import java.time.LocalDateTime;
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

import static online.talkandtravel.util.constants.ApiPathConstants.USERS_ONLINE_STATUS_ENDPOINT;
import static online.talkandtravel.util.RedisUtils.getUserIdFromRedisKey;

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
