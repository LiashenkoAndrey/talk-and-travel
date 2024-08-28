package online.talkandtravel.service;

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
 *   <li>{@link #joinChat(EventRequest)} - Processes a request for a user to join a chat. The method
 *       returns an {@link EventDtoBasic} representing the event that occurred, including details
 *       about the chat and user.
 *   <li>{@link #leaveChat(EventRequest)} - Processes a request for a user to leave a chat. The
 *       method returns an {@link EventDtoBasic} representing the event that occurred, including
 *       details about the chat and user.
 *   <li>{@link #startTyping(EventRequest)} - Processes a request indicating that a user has started
 *       typing in a chat. The method returns an {@link EventDtoBasic} representing the event that
 *       occurred, including details about the chat and user.
 *   <li>{@link #stopTyping(EventRequest)} - Processes a request indicating that a user has stopped
 *       typing in a chat. The method returns an {@link EventDtoBasic} representing the event that
 *       occurred, including details about the chat and user.
 * </ul>
 */
public interface EventService {

  void deleteChatIfEmpty(EventRequest request);

  MessageDto joinChat(EventRequest request);

  MessageDto leaveChat(EventRequest request);

  EventResponse startTyping(EventRequest request);

  EventResponse stopTyping(EventRequest request);
}
