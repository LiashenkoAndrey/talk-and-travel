package online.talkandtravel.controller.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.service.EventService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class EventController {

  private final EventService eventService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/events.joinChat")
  public void joinChat(@Payload EventRequest request) {
    log.info("create a new JOIN CHAT event {}", request);
    EventDtoBasic event = eventService.joinChat(request);

    messagingTemplate.convertAndSend("/countries/" + request.chatId() + "/events", event);
  }

  @MessageMapping("/events.leaveChat")
  public void leaveChat(@Payload EventRequest request) {
    log.info("create a new LEAVE CHAT event {}", request);
    EventDtoBasic event = eventService.leaveChat(request);

    messagingTemplate.convertAndSend("/countries/" + request.chatId() + "/events", event);
  }

  @MessageMapping("/events.startTyping")
  public void startTyping(@Payload EventRequest request) {
    log.info("create a new START TYPING event {}", request);
    EventDtoBasic event = eventService.startTyping(request);

    messagingTemplate.convertAndSend("/countries/" + request.chatId() + "/events", event);
  }

  @MessageMapping("/events.stopTyping")
  public void stopTyping(@Payload EventRequest request) {
    log.info("create a new STOP TYPING event {}", request);
    EventDtoBasic event = eventService.stopTyping(request);

    messagingTemplate.convertAndSend("/countries/" + request.chatId() + "/events", event);
  }
}
