package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link Message} entities and {@link MessageDtoBasic} data
 * transfer objects.
 *
 * <p>This interface uses MapStruct to define methods for mapping properties between {@link Message}
 * entities and {@link MessageDtoBasic} DTOs. It handles the conversion of message-related
 * information from the entity to the DTO.
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(config = MapperConfig.class,
    uses = {
        UserMapper.class
    })
public interface MessageMapper {

  @Mapping(target = "user", source = "sender")
  @Mapping(target = "repliedMessageId", source = "repliedMessage.id")
  @Mapping(target = "chatId", source = "chat.id")
  MessageDto toMessageDto(Message message);
}
