package online.talkandtravel.model.dto.event;

import java.time.LocalDateTime;
import online.talkandtravel.model.entity.EventType;
/**
 * Data Transfer Object (DTO) for representing a basic event.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the event.</li>
 *   <li>{@code authorId} - ID of the user who authored the event.</li>
 *   <li>{@code chatId} - ID of the chat where the event occurred.</li>
 *   <li>{@code eventType} - Type of the event (e.g., join, leave, typing).</li>
 *   <li>{@code eventTime} - Date and time when the event occurred.</li>
 * </ul>
 */

public record EventDtoBasic(Long id, Long authorId, Long chatId, EventType eventType, LocalDateTime eventTime) {}
