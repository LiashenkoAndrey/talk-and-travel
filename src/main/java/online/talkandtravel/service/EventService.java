package online.talkandtravel.service;

import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.event.JoinChatRequest;

public interface EventService {

  EventDtoBasic joinChat(JoinChatRequest request);
}
