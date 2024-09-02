package online.talkandtravel.model.enums;

/**
 * Enumeration representing the types of events that can occur in the chat application.
 *
 * <ul>
 *   <li>{@code JOIN} - User joins a chat.
 *   <li>{@code LEAVE} - User leaves a chat.
 *   <li>{@code START_TYPING} - User starts typing a message.
 *   <li>{@code STOP_TYPING} - User stops typing a message.
 * </ul>
 */
public enum MessageType {
  TEXT,
  JOIN,
  LEAVE,
  START_TYPING,
  STOP_TYPING
}
