package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatInfoDto;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper interface for converting between {@link UserChat} entities and {@link UserDtoBasic} data
 * transfer objects.
 *
 * <p>This interface uses MapStruct to define methods for mapping properties from a {@link UserChat}
 * entity to a {@link UserDtoBasic} DTO. It focuses on extracting user-related information from the
 * {@link UserChat} association and including it in the DTO.
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(config = MapperConfig.class, uses = {ChatMapper.class, MessageMapper.class})
public interface UserChatMapper {

  @Mapping(target = "chat", source = "chatInfoDto")
  @Mapping(target = "companion", source = "companion")
  @Mapping(target = "lastReadMessageId", source = "lastReadMessageId")
  @Mapping(target = "lastMessage", source = "lastMessage")
  PrivateChatDto toPrivateChatDto(PrivateChatInfoDto chatInfoDto, User companion, Message lastMessage, Long lastReadMessageId);
}
