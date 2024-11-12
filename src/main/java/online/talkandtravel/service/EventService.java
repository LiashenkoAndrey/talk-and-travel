package online.talkandtravel.service;

import java.security.Principal;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.event.EventResponse;
import online.talkandtravel.model.dto.message.MessageDto;

/**
 * Service interface for managing events related to chat interactions.
 *
 * <p>This service provides methods to handle various types of chat events, such as joining or
 * leaving a chat, and starting or stopping typing notifications.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #joinChat(EventRequest, Principal)} - Processes a request for a user to join a chat. The method
 *       returns an {@link EventDtoBasic} representing the event that occurred, including details
 *       about the chat and user.
 *   <li>{@link #leaveChat(EventRequest, Principal)} - Processes a request for a user to leave a chat. The
 *       method returns an {@link EventDtoBasic} representing the event that occurred, including
 *       details about the chat and user.
 *   <li>{@link #startTyping(EventRequest, Principal)} - Processes a request indicating that a user has started
 *       typing in a chat. The method returns an {@link EventDtoBasic} representing the event that
 *       occurred, including details about the chat and user.
 *   <li>{@link #stopTyping(EventRequest, Principal)} - Processes a request indicating that a user has stopped
 *       typing in a chat. The method returns an {@link EventDtoBasic} representing the event that
 *       occurred, including details about the chat and user.
 * </ul>
 */
public interface EventService {


  MessageDto joinChat(EventRequest request, Principal principal);

  MessageDto leaveChat(EventRequest request, Principal principal);

  EventResponse startTyping(EventRequest request, Principal principal);

  EventResponse stopTyping(EventRequest request, Principal principal);
}
