package online.talkandtravel.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.UserFacade;
import online.talkandtravel.model.entity.UserOnlineStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserStateController {

  private final UserFacade userFacade;

  @MessageMapping("/events.updateOnlineStatus")
  private void updateUserOnlineStatus(@Payload Boolean isOnline, Principal principal) {
    UserOnlineStatus onlineStatus = UserOnlineStatus.ofStatus(isOnline);
    userFacade.updateUserOnlineStatusAndNotifyAll(onlineStatus, principal);
  }
}
