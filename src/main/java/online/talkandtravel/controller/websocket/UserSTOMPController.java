package online.talkandtravel.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.UserIsTypingDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * A controller class that processes operations on the user
 *
 * <p>SEND endpoints:
 * <ul>
 *   <li>WS /chat/{chatId}/user/{userId}/texting-users: receives user that started or stopped texting
 * </ul>
 * <p>SUBSCRIBE endpoints:
 * <ul>
 *   <li>WS /countries/{chatId}/texting-users: sends user that started or stopped texting to
 *   all subscribed users
 * </ul>
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class UserSTOMPController {

  /**
   * Frontend subscribe on WS path /countries/{id}/texting-users and receive {@link UserIsTypingDTO}
   * if any user started or stopped typing Frontend sends a message to WS path
   * /chat/{id}/user/{id}/texting-users if user started or stopped typing
   *
   * @param dto {@link UserIsTypingDTO} with username and isTexting fields
   * @return {@link UserIsTypingDTO} full filled DTO to subscribed users
   */
  @MessageMapping("/{chatId}/user/{userId}/texting-users")
  @SendTo("/countries/{chatId}/texting-users")
  public UserIsTypingDTO onUserStartOrStopTyping(UserIsTypingDTO dto,
      @DestinationVariable("chatId") Long chatId,
      @DestinationVariable("userId") Long userId) {
    log.info("onUserStartOrStopTyping Dto! {}, chatId {}, userId {}", dto, chatId, userId);
    dto.setUserId(userId);
    dto.setChatId(chatId);
    return dto;
  }
}

