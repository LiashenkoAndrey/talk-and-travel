package online.talkandtravel.service;

import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.message.SendMessageRequest;

/**
 * Service interface for handling message operations within the application.
 *
 * <p>This service manages the saving and processing of messages sent by users. It provides methods
 * for creating new messages and associating them with the appropriate chat and sender.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #saveMessage(SendMessageRequest)} - Saves a new message based on the provided
 *       request data. The request includes details about the message content, the sender, and any
 *       replied message.
 * </ul>
 *
 * @param sendMessageRequest The request object containing the details needed to create a new
 *     message, including the message content, sender information, and any referenced (replied)
 *     message.
 * @return The saved message represented as a {@link MessageDtoBasic}, which includes essential
 *     information about the saved message.
 */
public interface MessageService {

  MessageDtoBasic saveMessage(SendMessageRequest sendMessageRequest);
}
