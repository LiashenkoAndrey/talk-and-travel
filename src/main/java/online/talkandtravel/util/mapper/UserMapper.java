package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link User} entities and various {@link User} data
 * transfer objects (DTOs).
 *
 * <p>This interface uses MapStruct to define methods for mapping properties between {@link User}
 * entities and {@link UserDtoWithAvatarAndPassword} and {@link UserDtoShort} DTOs. It includes
 * configurations for ignoring certain fields during mapping operations.
 *
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #toUserDtoWithAvatarAndPassword(User)} - Converts a {@link User} entity to a {@link
 *       UserDtoWithAvatarAndPassword}. This method excludes the password field from the DTO to
 *       enhance security.
 *   <li>{@link #mapToShortDto(User)} - Converts a {@link User} entity to a {@link UserDtoShort}.
 *       This method provides a simplified view of the user data.
 *   <li>{@link #mapToUser(UserDtoWithAvatarAndPassword)} - Converts a {@link
 *       UserDtoWithAvatarAndPassword} DTO back to a {@link User} entity. This method excludes
 *       certain fields such as tokens, role, and countries to prevent unnecessary data from being
 *       set in the user model.
 * </ul>
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(config = MapperConfig.class)
public interface UserMapper {

  @Mapping(target = "password", ignore = true)
  UserDtoWithAvatarAndPassword toUserDtoWithAvatarAndPassword(User user);

  UpdateUserResponse toUpdateUserResponse(User user);


  @Mapping(target = "id", source = "id")
  @Mapping(target = "userName", source = "userName")
  @Mapping(target = "userEmail", source = "userEmail")
  @Mapping(target = "about", source = "about")
  User mapToUser(UpdateUserRequest dto);


  UserDtoShort mapToShortDto(User user);

  @Mapping(target = "tokens", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "countries", ignore = true)
  User mapToUser(UserDtoWithAvatarAndPassword dto);


  UserDtoBasic toUserDtoBasic(User user);

  UserNameDto toUserNameDto(User user);
}
