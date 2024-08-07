package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface EventMapper {

  @Mapping(target = "authorId", source = "user.id")
  EventDtoBasic toEventDtoBasic(Event event);
}
