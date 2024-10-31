package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.AvatarService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper interface for converting between {@link User} entities and various {@link User} data
 * transfer objects (DTOs).
 *
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(config = MapperConfig.class)
public abstract class UserMapper {

  @Autowired
  private AvatarService avatarService;

  public abstract UpdateUserResponse toUpdateUserResponse(User user);

  public abstract void updateUser(UpdateUserRequest source, @MappingTarget User target);

  public abstract User registerRequestToUser(RegisterRequest request);

  public abstract User registerRequestToUser(SocialRegisterRequest request);

  @Mapping(target = "avatarUrl", expression = "java(generateAvatarUrl(user.getAvatar()))")
  public abstract UserDtoBasic toUserDtoBasic(User user);

  @Mapping(target = "avatarUrl", expression = "java(generateAvatarUrl(user.getAvatar()))")
  public abstract UserDtoShort toUserDtoShort(User user);

  @Mapping(target = "avatarUrl", expression = "java(generateAvatarUrl(user.getAvatar()))")
  public abstract UserNameDto toUserNameDto(User user);

  // Custom method to generate avatar URL
  public String generateAvatarUrl(Avatar avatar) {
    if (avatar == null) {
      return null;
    }
    return avatarService.generateImageUrl(avatar);
  }
}
