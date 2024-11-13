package online.talkandtravel.exception.attachment;

import online.talkandtravel.exception.model.HttpException;
import online.talkandtravel.model.entity.attachment.AttachmentType;
import org.springframework.http.HttpStatus;

public class UnsupportedAttachmentTypeException extends HttpException {

  private static final String MESSAGE = "Provided attachment type is not supported yet: %s, it must be in [IMAGE]";
  private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

  public UnsupportedAttachmentTypeException(AttachmentType attachmentType) {
    super(MESSAGE.formatted(attachmentType.toString()), STATUS);
  }

}