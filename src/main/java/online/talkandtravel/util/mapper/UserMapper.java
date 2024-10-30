package online.talkandtravel.util.mapper;

import static online.talkandtravel.util.constants.S3Constants.S3_BUCKET_NAME;
import static online.talkandtravel.util.constants.S3Constants.S3_URL_PATTERN;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between {@link User} entities and various {@link User} data
 * transfer objects (DTOs).
 *
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(config = MapperConfig.class)
public interface UserMapper {

  UpdateUserResponse toUpdateUserResponse(User user);

  void updateUser(UpdateUserRequest source, @MappingTarget User target);

  User registerRequestToUser(RegisterRequest request);

  User registerRequestToUser(SocialRegisterRequest request);

  @Mapping(target = "avatarUrl", expression = "java(generateAvatarUrl(user.getAvatar()))")
  UserDtoBasic toUserDtoBasic(User user);

  @Mapping(target = "avatarUrl", expression = "java(generateAvatarUrl(user.getAvatar()))")
  UserNameDto toUserNameDto(User user);

  default String generateAvatarUrl(Avatar avatar) {
    if (avatar == null) {
      return null;
    }
    return S3_URL_PATTERN.formatted(S3_BUCKET_NAME, avatar.getKey());
  }

}
