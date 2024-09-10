package online.talkandtravel.controller.websocket;

import static online.talkandtravel.util.service.EventServiceUtil.getUserFromPrincipal;

import java.security.Principal;
import jdk.jfr.BooleanFlag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.UserFacade;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.service.event.UserEventService;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserStateController {

  private final UserEventService userEventService;
  private final UserService userService;
  private final UserFacade userFacade;
  private final AuthenticationService authenticationService;

  @MessageMapping("/events.updateOnlineStatus")
  private void handle(@Payload Boolean isOnline, Principal principal) {
    log.info("/events.updateOnlineStatus");
    User user = getUserFromPrincipal(principal);
    CustomUserDetails details = new CustomUserDetails(user);
    authenticationService.authenticateUser(details, null);

    log.info("update online status, user id: {}, payload: {}", user.getId(), isOnline);

    UserOnlineStatus onlineStatus = UserOnlineStatus.ofStatus(isOnline);
    userFacade.updateUserOnlineStatusAndNotifyAll(onlineStatus);
  }

  @SubscribeMapping("/{userId}/onlineStatus")
  private void sendActualUserOnlineStatus(@DestinationVariable("userId") Long userId) {
    log.info("Send actual user online status to user {}", userId);
    UserOnlineStatusDto onlineStatus = userService.getUserOnlineStatus(userId);
    userEventService.publishUserOnlineStatusEvent(UserOnlineStatus.ofStatus(onlineStatus.isOnline()), userId);
  }
}
