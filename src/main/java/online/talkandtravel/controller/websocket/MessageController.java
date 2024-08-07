package online.talkandtravel.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MessageController {

  private final SimpMessagingTemplate messagingTemplate;
  private final ChatService chatService;
  private final MessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(@Payload SendMessageRequest request) {
    log.info("create a new message {}", request);
    MessageDtoBasic message = messageService.saveMessage(request);
    ChatDto chatDto = chatService.findChatById(request.chatId());

    messagingTemplate.convertAndSend("/countries/" + chatDto.id() + "/messages", message);
  }
}
