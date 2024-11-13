package online.talkandtravel.facade;

import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageWithAttachmentRequest;

public interface MessageFacade {

 MessageDto saveMessageWithAttachment(SendMessageWithAttachmentRequest file);

}
