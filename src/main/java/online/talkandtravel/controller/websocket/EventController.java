package online.talkandtravel.controller.websocket;

import static online.talkandtravel.util.constants.ApiPathConstants.JOIN_CHAT_EVENT_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.LEAVE_CHAT_EVENT_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.MESSAGES_SUBSCRIBE_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.START_TYPING_EVENT_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.STOP_TYPING_EVENT_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.UPDATE_ONLINE_STATUS_EVENT_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.USERS_ONLINE_STATUS_ENDPOINT;

import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.event.EventResponse;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.service.EventService;
import online.talkandtravel.service.OnlineService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
  private final OnlineService onlineService;

  @MessageMapping(UPDATE_ONLINE_STATUS_EVENT_PATH)
  @SendTo(USERS_ONLINE_STATUS_ENDPOINT)
  public OnlineStatusDto updateUserOnlineStatus(@Valid @Payload Boolean isOnline, Principal principal) {
    return onlineService.updateUserOnlineStatus(principal, isOnline);
  }

  @MessageMapping(JOIN_CHAT_EVENT_PATH)
  public void joinChat(@Valid @Payload EventRequest request, Principal principal) {
    log.info("create a new JOIN CHAT event {}, {}", request, principal);
    MessageDto message = eventService.joinChat(request, principal);
    sendResponse(request, message);
  }

  @MessageMapping(LEAVE_CHAT_EVENT_PATH)
  public void leaveChat(@Valid @Payload EventRequest request, Principal principal) {
    log.info("create a new LEAVE CHAT event {}", request);
    MessageDto message = eventService.leaveChat(request, principal);
    sendResponse(request, message);
  }

  @MessageMapping(START_TYPING_EVENT_PATH)
  public void startTyping(@Valid @Payload EventRequest request, Principal principal) {
    log.info("create a new START TYPING event {}", request);
    EventResponse message = eventService.startTyping(request, principal);

    sendResponse(request, message);
  }

  @MessageMapping(STOP_TYPING_EVENT_PATH)
  public void stopTyping(@Valid @Payload EventRequest request, Principal principal) {
    log.info("create a new STOP TYPING event {}", request);
    EventResponse message = eventService.stopTyping(request, principal);

    sendResponse(request, message);
  }

  private <T> void sendResponse(EventRequest request, T message) {
    if (message != null) {
      messagingTemplate.convertAndSend(MESSAGES_SUBSCRIBE_PATH.formatted(request.chatId()), message);
    }
  }
}
