package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface MessageMapper {

  @Mapping(target = "chatId", source = "chat.id")
  @Mapping(target = "senderId", source = "sender.id")
  @Mapping(target = "repliedMessageId", source = "repliedMessage.id")
  MessageDtoBasic toMessageDtoBasic(Message message);
}
