package online.talkandtravel.facade;

import online.talkandtravel.model.dto.message.SendMessageWithAttachmentRequest;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.attachment.AttachmentType;
import online.talkandtravel.util.FileDto;

public interface MessageFacade {

 void saveMessageWithAttachment(SendMessageWithAttachmentRequest file, FileDto fileDto, User sender);

 void validateAttachmentType(AttachmentType attachmentType);

}
