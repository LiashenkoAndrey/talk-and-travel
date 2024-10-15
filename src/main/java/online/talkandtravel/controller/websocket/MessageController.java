package online.talkandtravel.controller.websocket;

import static online.talkandtravel.util.constants.ApiPathConstants.MESSAGES_SUBSCRIBE_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.SEND_MESSAGE_PATH;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling WebSocket messages related to chat functionalities.
 *
 * <ul>
 *   <li>{@code sendMessage} - Processes a request to send a message in a chat, saves the message,
 *       and broadcasts it to relevant subscribers.
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class MessageController {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;

  @MessageMapping(SEND_MESSAGE_PATH)
  public void sendMessage(@Valid  @Payload SendMessageRequest request, Principal principal) {
    log.info("create a new message {}", request);
    MessageDto message = messageService.saveMessage(request, principal);
    messagingTemplate.convertAndSend(MESSAGES_SUBSCRIBE_PATH.formatted(request.chatId()), message);
  }
}
