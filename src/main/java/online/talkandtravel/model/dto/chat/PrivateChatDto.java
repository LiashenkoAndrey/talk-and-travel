package online.talkandtravel.model.dto.chat;

import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserDtoShort;

public record PrivateChatDto(PrivateChatInfoDto chat, UserDtoShort companion, Long lastReadMessageId, MessageDto lastMessage) {

  public PrivateChatDto(PrivateChatInfoDto chat, UserDtoShort companion, Long lastReadMessageId) {
    this(chat, companion, lastReadMessageId, null);
  }
}
