package online.talkandtravel.model.dto.event;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for representing a request for an event.
 *
 * <ul>
 *   <li>{@code authorId} - ID of the user making the event request.</li>
 *   <li>{@code chatId} - ID of the chat related to the event request.</li>
 * </ul>
 */

public record EventRequest(
    @NotNull Long chatId
) {

}
