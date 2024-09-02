package online.talkandtravel.controller.websocket;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.event.EventResponse;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.service.EventService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling WebSocket events related to chat interactions.
 *
 * <ul>
 *   <li>{@code joinChat} - Processes a request to join a chat and sends an event notification to
 *       relevant subscribers.
 *   <li>{@code leaveChat} - Processes a request to leave a chat and sends an event notification to
 *       relevant subscribers.
 *   <li>{@code startTyping} - Processes a request indicating that a user has started typing in a
 *       chat and sends an event notification.
 *   <li>{@code stopTyping} - Processes a request indicating that a user has stopped typing in a
 *       chat and sends an event notification.
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class EventController {

  private final EventService eventService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/events.joinChat")
  public void joinChat(@Payload @Valid EventRequest request, Principal principal) {
    log.info("create a new JOIN CHAT event {}, {}", request, principal);
    MessageDto message = eventService.joinChat(request, principal);

    sendResponse(request, message);
  }

  @MessageMapping("/events.leaveChat")
  public void leaveChat(@Payload EventRequest request, Principal principal) {
    log.info("create a new LEAVE CHAT event {}", request);
    MessageDto message = eventService.leaveChat(request, principal);
    eventService.deleteChatIfEmpty(request, principal);
    sendResponse(request, message);
  }

  @MessageMapping("/events.startTyping")
  public void startTyping(@Payload EventRequest request, Principal principal) {
    log.info("create a new START TYPING event {}", request);
    EventResponse message = eventService.startTyping(request, principal);

    sendResponse(request, message);
  }

  @MessageMapping("/events.stopTyping")
  public void stopTyping(@Payload EventRequest request, Principal principal) {
    log.info("create a new STOP TYPING event {}", request);
    EventResponse message = eventService.stopTyping(request, principal);

    sendResponse(request, message);
  }

  private void sendResponse(EventRequest request, MessageDto message) {
    messagingTemplate.convertAndSend("/countries/" + request.chatId() + "/messages", message);
  }

  private void sendResponse(EventRequest request, EventResponse message) {
    messagingTemplate.convertAndSend("/countries/" + request.chatId() + "/messages", message);
  }
}
