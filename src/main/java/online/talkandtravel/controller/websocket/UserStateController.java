package online.talkandtravel.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.UserService;
import online.talkandtravel.service.event.UserEventService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserStateController {

  private final UserService userService;
  private final UserEventService userEventService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  @SubscribeMapping("/userOnlineStatus/user/{userId}/onlineStatus")
  private void sendActualUserOnlineStatus(@DestinationVariable("userId") Long userId) {
    log.info("Send actual user online status to user {}", userId);
    simpMessagingTemplate.convertAndSend("/chat/userOnlineStatus/user/8/onlineStatus", true);
    simpMessagingTemplate.convertAndSend("/userOnlineStatus/user/8/onlineStatus", true);
//    UserOnlineStatusDto dto = userService.getUserOnlineStatus(userId);
    userEventService.publishEvent(UserOnlineStatus.ONLINE, userId);
  }
}
