package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.UserChat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserChatMapper {

  @Mapping(target = "userName", source = "user.userName")
  @Mapping(target = "userEmail", source = "user.userEmail")
  @Mapping(target = "about", source = "user.about")
  UserDtoBasic toUserDtoBasic(UserChat userChat);
}
