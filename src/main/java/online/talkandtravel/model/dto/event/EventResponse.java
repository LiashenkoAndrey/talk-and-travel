package online.talkandtravel.model.dto.event;

import java.time.LocalDateTime;
import online.talkandtravel.model.entity.EventType;

/**
 * Data Transfer Object (DTO) for representing a basic event.
 *
 * <ul>
 *   <li>{@code authorId} - ID of the user who authored the event.
 *   <li>{@code eventType} - Type of the event (e.g., start_typing, stop_typing).
 *   <li>{@code eventTime} - Date and time when the event occurred.
 * </ul>
 */
public record EventResponse(Long authorId, EventType eventType, LocalDateTime eventTime) {}
