package online.talkandtravel.exception.country;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a specified country is not found.
 *
 * <p>This exception is used to indicate that a request involving a specific country cannot be
 * processed because the country does not exist in the system. It extends {@link ApiException} to
 * fit within the application's exception handling framework, providing both a detailed message and
 * an HTTP status code.
 *
 * <p>The exception message is formatted with the name of the country that could not be found. The
 * HTTP status is set to 404 (Not Found) to indicate that the requested resource is missing.
 */
public class CountryNotFoundException extends ApiException {

  private static final String MESSAGE = "Country [%s] not found";
  private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

  public CountryNotFoundException(String country) {
    super(MESSAGE.formatted(country), STATUS);
  }
}
