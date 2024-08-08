package online.talkandtravel.exception.chat;

import online.talkandtravel.exception.model.ApiException;

public class ChatNotFoundException extends ApiException {

  private static final String MESSAGE = "Chat with id %s not found";

  public ChatNotFoundException(Long chatId) {
    super(MESSAGE.formatted(chatId));
  }
}
