package online.talkandtravel.model.dto.chat;

import java.time.ZonedDateTime;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.entity.ChatType;

/**
 * Data Transfer Object (DTO) for representing a chat.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the chat.
 *   <li>{@code name} - Name of the chat.
 *   <li>{@code description} - Description of the chat.
 *   <li>{@code chatType} - Type of the chat (e.g., group, private).
 *   <li>{@code creationDate} - Date and time when the chat was created.
 *   <li>{@code usersCount} - Amount of users participating in the chat.
 *   <li>{@code messages} - List of messages exchanged in the chat.
 *   <li>{@code events} - List of events related to the chat.
 * </ul>
 */
public record ChatDto(
    Long id,
    String name,
    String description,
    CountryInfoDto country,
    ChatType chatType,
    ZonedDateTime creationDate,
    Long usersCount,
    Long messagesCount,
    Long unreadMessagesCount
) {

  public ChatDto(String name) {
    this(null, name, null, null, null, null, null, null, null);
  }
}
