package online.talkandtravel.util.mapper;

import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDtoWithAvatarAndPassword toUserDtoWithAvatarAndPassword(User user);

    UserDtoShort mapToShortDto(User user);

    User mapToModel(UserDtoWithAvatarAndPassword dto);
}
