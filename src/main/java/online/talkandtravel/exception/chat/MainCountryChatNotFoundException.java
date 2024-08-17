package online.talkandtravel.exception.chat;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the main chat for a specified country is not found.
 *
 * <p>This exception indicates that the requested main chat associated with a particular country
 * does not exist in the system. It extends {@link HttpException} to integrate with the application's
 * exception handling framework, providing both a specific message and an HTTP status code for
 * clarity and client communication.
 *
 * <p>The exception includes a message formatted with the name of the country for which the main
 * chat was not found and an HTTP status of 404 (Not Found), suitable for cases where the requested
 * resource is missing.
 */
public class MainCountryChatNotFoundException extends HttpException {

  private static final String MESSAGE = "Main country chat not found for [%s]";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public MainCountryChatNotFoundException(String countryName) {
    super(MESSAGE.formatted(countryName), STATUS);
  }
}
