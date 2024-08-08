package online.talkandtravel.exception.data;

import online.talkandtravel.exception.model.ApiException;

public class FailedToReadJsonException extends ApiException {

  private static final String MESSAGE = "Failed to parse JSON file at %s";

  public FailedToReadJsonException(String jsonFilePath) {
    super(MESSAGE.formatted(jsonFilePath));
  }
}
