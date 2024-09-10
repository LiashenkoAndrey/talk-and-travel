package online.talkandtravel.model.dto.chat;

import online.talkandtravel.model.dto.user.UserDtoShortWithOnline;

public record PrivateChatDto(
    PrivateChatInfoDto chat,
    UserDtoShortWithOnline companion,
    Long lastReadMessageId) {


}
