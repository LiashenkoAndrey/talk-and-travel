package online.talkandtravel.exception.message;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class MessageFromAnotherChatException extends HttpException {

  private static final String MESSAGE = "Message with id %s belongs to chat with id %s, but it was expected that this message belongs to the chat with id %s";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public MessageFromAnotherChatException(Long messageId, Long expectedChatId, Long actualChatId ) {
    super(String.format(MESSAGE, messageId, actualChatId, expectedChatId), STATUS);
  }

}
