package online.talkandtravel.service;

import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.event.EventRequest;

public interface EventService {

  EventDtoBasic joinChat(EventRequest request);

  EventDtoBasic leaveChat(EventRequest request);

  EventDtoBasic startTyping(EventRequest request);

  EventDtoBasic stopTyping(EventRequest request);
}
