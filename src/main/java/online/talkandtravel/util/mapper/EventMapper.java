package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link Event} entities and {@link EventDtoBasic} data
 * transfer objects.
 *
 * <p>This interface uses MapStruct to define methods for mapping properties between {@link Event}
 * entities and {@link EventDtoBasic} DTOs. It handles the conversion of event-related information
 * from the entity to the DTO.
 *
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #toEventDtoBasic(Event)} - Converts an {@link Event} entity to an {@link
 *       EventDtoBasic}. This method maps the properties of the {@link Event} entity to the DTO.
 *       Specifically, it extracts the chat ID and author ID from the associated {@link Chat} and
 *       {@link User} entities, respectively, and includes them in the DTO.
 * </ul>
 *
 * <p>This mapper relies on {@link MapperConfig} to apply global mapping settings.
 *
 */
@Mapper(config = MapperConfig.class)
public interface EventMapper {

  @Mapping(target = "chatId", source = "chat.id")
  @Mapping(target = "authorId", source = "user.id")
  EventDtoBasic toEventDtoBasic(Event event);
}
