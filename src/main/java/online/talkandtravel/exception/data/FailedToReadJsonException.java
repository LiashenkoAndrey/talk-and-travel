package online.talkandtravel.exception.data;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a JSON file fails to be parsed or read.
 *
 * <p>This exception is used to indicate that there was an error in processing a JSON file,
 * typically due to issues with the file format or content. It extends {@link ApiException} to
 * integrate with the application's error handling mechanism, providing both a descriptive message
 * and an HTTP status code.
 *
 * <p>The exception message is formatted with the file path of the JSON file that could not be read.
 * The HTTP status is set to 409 (Conflict) to reflect that the request could not be completed due
 * to a conflict with the current state of the resource.
 */
public class FailedToReadJsonException extends ApiException {

  private static final String MESSAGE = "Failed to parse JSON file at %s";
  private static final HttpStatus STATUS = HttpStatus.CONFLICT;

  public FailedToReadJsonException(String jsonFilePath) {
    super(MESSAGE.formatted(jsonFilePath), STATUS);
  }
}
