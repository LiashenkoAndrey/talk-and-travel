package online.talkandtravel.util.mapper;

import static online.talkandtravel.util.constants.S3Constants.AVATAR_X256_FOLDER_NAME;
import static online.talkandtravel.util.constants.S3Constants.AVATAR_X50_FOLDER_NAME;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.service.AvatarService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(config = MapperConfig.class)
public abstract class AvatarMapper {

  @Autowired
  @Lazy
  private AvatarService avatarService;

  protected static final String AVATAR_DIMENSION_50x50 = AVATAR_X50_FOLDER_NAME;

  protected static final String AVATAR_DIMENSION_256x256 = AVATAR_X256_FOLDER_NAME;

  @Mapping(target = "image50x50", expression = "java(generateAvatarUrl(avatar, AVATAR_DIMENSION_50x50))")
  @Mapping(target = "image256x256", expression = "java(generateAvatarUrl(avatar, AVATAR_DIMENSION_256x256))")
  public abstract AvatarDto toAvatarDto(Avatar avatar);

  // Custom method to generate avatar URL
  public String generateAvatarUrl(Avatar avatar, String avatarDimension) {
    if (avatar == null) {
      return null;
    }
    return avatarService.generateImageUrl(avatar, avatarDimension);
  }
}
