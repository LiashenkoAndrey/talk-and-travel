package online.talkandtravel.model.dto.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetLastReadMessageRequest(
    @NotNull @Positive Long userId,
    @NotNull @Positive Long lastReadMessageId,
    @Valid NewPrivateChatDto newPrivateChatDto)
{}
