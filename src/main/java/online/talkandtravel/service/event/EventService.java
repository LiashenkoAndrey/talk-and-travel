package online.talkandtravel.service.event;

import online.talkandtravel.model.dto.event.EventRequest;

public interface EventService {

  void publishEvent(EventRequest request, Object payload);

}
