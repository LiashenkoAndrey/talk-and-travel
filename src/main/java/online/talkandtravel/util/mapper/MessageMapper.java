package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.attachment.AttachmentDto;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.attachment.Image;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

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
        UserMapper.class,
        AttachmentMapper.class
    })
public abstract class MessageMapper {

  @Autowired
  private AttachmentMapper attachmentMapper;

  @Mapping(target = "user", source = "sender")
  @Mapping(target = "repliedMessageId", source = "repliedMessage.id")
  @Mapping(target = "chatId", source = "chat.id")
  @Mapping(target = "attachment", expression = "java(mapAttachment2(message))")
  public abstract MessageDto toMessageDto(Message message);

  public AttachmentDto mapAttachment2(Message att) {
    return attachmentMapper.toImageAttachmentDto((Image) att.getAttachment(), att.getChat().getId());
  }
}
