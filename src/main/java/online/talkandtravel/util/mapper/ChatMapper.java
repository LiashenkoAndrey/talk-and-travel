package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.PrivateChatInfoDto;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.UserChat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link Chat} entities and {@link ChatDto} data transfer
 * objects.
 *
 * <p>This interface uses MapStruct to define mapping methods that convert {@link Chat} and {@link
 * UserChat} entities to their corresponding DTO representations. It also handles the mapping of
 * nested objects such as users, messages, events, and other related fields.
 *
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #toDto(Chat)} - Converts a {@link Chat} entity to a {@link ChatDto}. This method
 *       maps the properties of the {@link Chat} entity to the DTO, including nested collections
 *       like users and messages.
 *   <li>{@link #userChatToChatInfoDto(UserChat)} - Converts a {@link UserChat} entity to a {@link
 *       PrivateChatInfoDto}. This method handles the mapping of properties from {@link UserChat} to {@link
 *       PrivateChatInfoDto}, including fields such as name, description, and creation date.
 * </ul>
 *
 * <p>This mapper relies on other mappers for converting nested objects, such as {@link UserMapper},
 *  {@link MessageMapper}, and {@link UserChatMapper}. It is configured with
 * {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(
    config = MapperConfig.class,
    uses = {
      UserMapper.class,
      MessageMapper.class,
      UserChatMapper.class
    })
public interface ChatMapper {

  @Mapping(target = "usersCount", expression = "java((long) chat.getUsers().size())")
  @Mapping(target = "messagesCount", expression = "java((long) chat.getMessages().size())")
  @Mapping(target = "unreadMessagesCount", source = "unreadMessagesCount")
  ChatDto toDto(Chat chat, Long unreadMessagesCount);

  @Mapping(target = "messagesCount", expression = "java((long) chat.getMessages().size())")
  @Mapping(target = "usersCount", expression = "java((long) chat.getUsers().size())")
  ChatInfoDto toChatInfoDto(Chat chat);

  @Mapping(target = "messagesCount", expression = "java((long) chat.getMessages().size())")
  @Mapping(target = "usersCount", expression = "java((long) chat.getUsers().size())")
  @Mapping(target = "unreadMessagesCount", source = "unreadMessagesCount")
  PrivateChatInfoDto chatToPrivateChatInfoDto(Chat chat, Long unreadMessagesCount);

  @Mapping(target = "usersCount", expression = "java((long) userChat.getChat().getUsers().size())")
  @Mapping(
      target = "messagesCount",
      expression = "java((long) userChat.getChat().getMessages().size())")
  @Mapping(target = "description", source = "chat.description")
  @Mapping(target = "creationDate", source = "chat.creationDate")
  @Mapping(target = "chatType", source = "chat.chatType")
  @Mapping(target = "name", source = "chat.name")
  ChatInfoDto userChatToChatInfoDto(UserChat userChat);
}
