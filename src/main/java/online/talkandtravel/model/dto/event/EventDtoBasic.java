package online.talkandtravel.model.dto.event;

import java.time.LocalDateTime;
import online.talkandtravel.model.enums.MessageType;
/**
 * Data Transfer Object (DTO) for representing a basic event.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the event.</li>
 *   <li>{@code authorId} - ID of the user who authored the event.</li>
 *   <li>{@code chatId} - ID of the chat where the event occurred.</li>
 *   <li>{@code type} - Type of the event (e.g., join, leave, typing).</li>
 *   <li>{@code creationDate} - Date and time when the event occurred.</li>
 * </ul>
 */

public record EventDtoBasic(Long id, Long authorId, Long chatId, MessageType messageType, LocalDateTime eventTime) {}
