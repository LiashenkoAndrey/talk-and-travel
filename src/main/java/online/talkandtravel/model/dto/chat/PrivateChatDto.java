package online.talkandtravel.model.dto.chat;

import online.talkandtravel.model.dto.user.UserDtoShort;

public record PrivateChatDto(ChatInfoDto chat, UserDtoShort companion, Long lastReadMessageId) {}
