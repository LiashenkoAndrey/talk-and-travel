package online.talkandtravel.model.dto.chat;

import java.time.LocalDateTime;
import java.util.List;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.ChatType;
/**
 * Data Transfer Object (DTO) for representing a chat.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the chat.</li>
 *   <li>{@code name} - Name of the chat.</li>
 *   <li>{@code description} - Description of the chat.</li>
 *   <li>{@code chatType} - Type of the chat (e.g., group, private).</li>
 *   <li>{@code creationDate} - Date and time when the chat was created.</li>
 *   <li>{@code users} - List of users participating in the chat.</li>
 *   <li>{@code messages} - List of messages exchanged in the chat.</li>
 *   <li>{@code events} - List of events related to the chat.</li>
 * </ul>
 */

public record ChatDto(
    Long id,
    String name,
    String description,
    ChatType chatType,
    LocalDateTime creationDate,
    List<UserDtoBasic> users,
    List<MessageDtoBasic> messages,
    List<EventDtoBasic> events) {}
