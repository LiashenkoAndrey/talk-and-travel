package online.talkandtravel.service;

import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.message.SendMessageRequest;

public interface MessageService {

  MessageDtoBasic saveMessage(SendMessageRequest sendMessageRequest);
}
