package online.talkandtravel.service;

import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.message.SendMessageRequest;

import java.security.Principal;

/**
 * Service interface for handling message operations within the application.
 *
 * <p>This service manages the saving and processing of messages sent by users. It provides methods
 * for creating new messages and associating them with the appropriate chat and sender.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #saveMessage(SendMessageRequest, Principal)} - Saves a new message based on the provided
 *       request data. The request includes details about the message content, the sender, and any
 *       replied message.
 * </ul>
 */
public interface MessageService {

  MessageDto saveMessage(SendMessageRequest sendMessageRequest, Principal principal);
}
