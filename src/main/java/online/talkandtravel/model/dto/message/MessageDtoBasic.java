package online.talkandtravel.model.dto.message;

import java.time.LocalDateTime;
/**
 * Data Transfer Object (DTO) for representing a basic message.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the message.</li>
 *   <li>{@code content} - Content of the message.</li>
 *   <li>{@code creationDate} - Date and time when the message was created.</li>
 *   <li>{@code senderId} - ID of the user who sent the message.</li>
 *   <li>{@code chatId} - ID of the chat where the message was sent.</li>
 *   <li>{@code repliedMessageId} - ID of the message being replied to, if any.</li>
 * </ul>
 */

public record MessageDtoBasic(
    Long id, String content, LocalDateTime creationDate, Long senderId, Long chatId, Long repliedMessageId) {}
