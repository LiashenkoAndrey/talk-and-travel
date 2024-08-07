package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.entity.Chat;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ChatMapper {

  ChatDto toDto(Chat chat);
}
