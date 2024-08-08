package online.talkandtravel.exception.message;

import online.talkandtravel.exception.model.ApiException;

public class MessageNotFoundException extends ApiException {

  private static final String MESSAGE = "Message with id %s not found";

  public MessageNotFoundException(Long messageId) {
    super(String.format(MESSAGE, messageId));
  }
}
