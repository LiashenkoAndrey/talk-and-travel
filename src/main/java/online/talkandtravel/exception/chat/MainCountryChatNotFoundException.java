package online.talkandtravel.exception.chat;

import online.talkandtravel.exception.model.ApiException;

public class MainCountryChatNotFoundException extends ApiException {

  private static final String MESSAGE = "Main country chat not found for [%s]";

  public MainCountryChatNotFoundException(String countryName) {
    super(MESSAGE.formatted(countryName));
  }
}
