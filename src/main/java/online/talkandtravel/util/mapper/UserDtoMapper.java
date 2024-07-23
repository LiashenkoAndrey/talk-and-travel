package online.talkandtravel.util.mapper;

import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    @Mapping(target = "password", ignore = true)
    UserDto mapToDto(User user);

    User mapToModel(UserDto dto);
}
