package online.talkandtravel.controller.websocket;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.UserIsTypingDTORequest;
import online.talkandtravel.model.dto.UserIsTypingDTOResponse;
import online.talkandtravel.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

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

  private final UserService userService;

  /**
   * If any user started or stopped typing Frontend sends a message to WS path
   * /chat/{chatId}/user/{userId}/texting-users. Frontend subscribe on WS path
   * /countries/{countryId}/texting-users and receive {@link UserIsTypingDTOResponse}
   *
   * @param dto {@link UserIsTypingDTORequest} with username and isTexting fields
   */
  @MessageMapping("/{chatId}/user/{userId}/texting-users")
  public void onUserStartOrStopTyping(@Validated UserIsTypingDTORequest dto,
      @NotNull @DestinationVariable("chatId") Long chatId,
      @NotNull @DestinationVariable("userId") Long userId) {
    userService.notifyAllThatUserStartOrStopTyping(chatId, userId, dto.getUserName(),
        dto.getUserIsTexting());
  }
}

