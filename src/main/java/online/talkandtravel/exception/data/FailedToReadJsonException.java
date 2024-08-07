package online.talkandtravel.exception.data;

public class FailedToReadJsonException extends RuntimeException {

  private static final String MESSAGE = "Failed to parse JSON file at %s";

  public FailedToReadJsonException(String jsonFilePath) {
    super(MESSAGE.formatted(jsonFilePath));
  }
}
