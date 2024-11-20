package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.attachment.AttachmentDto;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.MessageDtoShort;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.attachment.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
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
  @Mapping(target = "chatId", source = "chat.id")
  @Mapping(target = "attachment", expression = "java(mapAttachment(message))")
  public abstract MessageDto toMessageDto(Message message);

  @Mapping(target = "user", source = "sender")
  @Mapping(target = "attachment", expression = "java(mapAttachment(message))")
  public abstract MessageDtoShort toMessageDtoShort(Message message);

  public AttachmentDto mapAttachment(Message message) {
    if (message.getAttachment() == null) {
      return null;
    }
    return attachmentMapper.toImageAttachmentDto((Image) message.getAttachment(), message.getChat().getId());
  }
}

