package online.talkandtravel.model.dto.chat;

import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserDtoShort;

public record PrivateChatDto(
    PrivateChatInfoDto chat,
    UserDtoShort companion,
    MessageDto lastMessage
) {

}
