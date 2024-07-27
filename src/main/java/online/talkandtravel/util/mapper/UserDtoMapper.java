package online.talkandtravel.util.mapper;

import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.UserDtoWithAvatarAndPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    @Mapping(target = "password", ignore = true)
    UserDtoWithAvatarAndPassword mapToDto(User user);

    User mapToModel(UserDtoWithAvatarAndPassword dto);
}
