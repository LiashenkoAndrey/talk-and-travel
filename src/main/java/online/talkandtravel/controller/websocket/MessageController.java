package online.talkandtravel.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
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
 *   <li>{@code sendMessage} - Processes a request to send a message in a chat, saves the message, and broadcasts it to relevant subscribers.</li>
 * </ul>
 */

@RestController
@RequiredArgsConstructor
@Log4j2
public class MessageController {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(@Payload SendMessageRequest request) {
    log.info("create a new message {}", request);
    MessageDtoBasic message = messageService.saveMessage(request);

    messagingTemplate.convertAndSend("/countries/" + request.chatId() + "/messages", message);
  }
}
