package online.talkandtravel.model.dto.event;

import java.time.ZonedDateTime;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.MessageType;

/**
 * Data Transfer Object (DTO) for representing a basic event.
 *
 * <ul>
 *   <li>{@code authorId} - ID of the user who authored the event.
 *   <li>{@code type} - Type of the event (e.g., start_typing, stop_typing).
 *   <li>{@code creationDate} - Date and time when the event occurred.
 * </ul>
 */
public record EventResponse(
    UserNameDto user,
    MessageType type,
    ZonedDateTime creationDate
) {

}
