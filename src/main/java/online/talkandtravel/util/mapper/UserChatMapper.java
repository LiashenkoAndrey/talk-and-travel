package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.UserChat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link UserChat} entities and {@link UserDtoBasic} data
 * transfer objects.
 *
 * <p>This interface uses MapStruct to define methods for mapping properties from a {@link UserChat}
 * entity to a {@link UserDtoBasic} DTO. It focuses on extracting user-related information from the
 * {@link UserChat} association and including it in the DTO.
 *
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #toUserDtoBasic(UserChat)} - Converts a {@link UserChat} entity to a {@link
 *       UserDtoBasic}. This method maps user-related properties such as userName, userEmail, and
 *       about from the associated {@link UserDtoBasic} entity within {@link UserChat} to the DTO.
 * </ul>
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(config = MapperConfig.class, uses = {ChatMapper.class})
public interface UserChatMapper {

  @Mapping(target = "userName", source = "user.userName")
  @Mapping(target = "userEmail", source = "user.userEmail")
  @Mapping(target = "about", source = "user.about")
  UserDtoBasic toUserDtoBasic(UserChat userChat);

  @Mapping(target = "companion", source = "user")
  PrivateChatDto toPrivateChatDto(UserChat userChat);

}
