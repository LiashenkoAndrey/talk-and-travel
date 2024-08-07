package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDtoWithAvatarAndPassword toUserDtoWithAvatarAndPassword(User user);

    UserDtoShort mapToShortDto(User user);

    @Mapping(target = "tokens", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "countries", ignore = true)
    User mapToModel(UserDtoWithAvatarAndPassword dto);

    UserDtoBasic toUserDtoBasic(User user);
}
