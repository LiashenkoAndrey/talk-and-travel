package online.talkandtravel.controller.websocket;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.OnlineService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserEventController {

  private final OnlineService onlineService;

  @MessageMapping("/events.updateOnlineStatus")
  private void updateUserOnlineStatus(@Payload Boolean isOnline, Principal principal) {
    onlineService.updateUserOnlineStatus(principal, isOnline);
  }
}
