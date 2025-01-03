package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between {@link User} entities and various {@link User} data
 * transfer objects (DTOs).
 *
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(
    config = MapperConfig.class,
    uses = AvatarMapper.class
)
public interface UserMapper {

  UpdateUserResponse toUpdateUserResponse(User user);

  void updateUser(UpdateUserRequest source, @MappingTarget User target);

  User registerRequestToUser(RegisterRequest request);

  User registerRequestToUser(SocialRegisterRequest request);

  UserDtoBasic toUserDtoBasic(User user);

  UserDtoShort toUserDtoShort(User user);

  UserNameDto toUserNameDto(User user);
}
