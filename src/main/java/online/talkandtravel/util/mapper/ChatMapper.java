package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.UserChat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    config = MapperConfig.class,
    uses = {UserMapper.class, EventMapper.class, MessageMapper.class, UserChatMapper.class})
public interface ChatMapper {

  ChatDto toDto(Chat chat);

  @Mapping(target = "users", source = "chat.users")
  @Mapping(target = "name", source = "chat.name")
  @Mapping(target = "messages", source = "chat.messages")
  @Mapping(target = "events", source = "chat.events")
  @Mapping(target = "description", source = "chat.description")
  @Mapping(target = "creationDate", source = "chat.creationDate")
  @Mapping(target = "chatType", source = "chat.chatType")
  ChatDto toDto(UserChat userChat);
}
