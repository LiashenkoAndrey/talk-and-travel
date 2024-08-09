package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
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
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #toMessageDtoBasic(Message)} - Converts a {@link Message} entity to a {@link
 *       MessageDtoBasic}. This method maps the properties of the {@link Message} entity to the DTO.
 *       Specifically, it extracts the chat ID, sender ID, and replied message ID from the
 *       associated {@link Chat}, {@link User}, and {@link Message} entities, respectively, and
 *       includes them in the DTO.
 * </ul>
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 */
@Mapper(config = MapperConfig.class)
public interface MessageMapper {

  @Mapping(target = "chatId", source = "chat.id")
  @Mapping(target = "senderId", source = "sender.id")
  @Mapping(target = "repliedMessageId", source = "repliedMessage.id")
  MessageDtoBasic toMessageDtoBasic(Message message);
}
