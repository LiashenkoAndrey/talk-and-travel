package online.talkandtravel.model.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetLastReadMessageDtoRequest (
    @NotNull @Positive Long userId,
    @NotNull @Positive Long lastReadMessageId)
{}
