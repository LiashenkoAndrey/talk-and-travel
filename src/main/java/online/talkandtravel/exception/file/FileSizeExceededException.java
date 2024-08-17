package online.talkandtravel.exception.file;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a file exceeds the maximum allowed size.
 *
 * <p>This exception is used to indicate that the uploaded file is larger than the permitted size
 * limit. It extends {@link HttpException} and is intended to be used when a file upload operation
 * fails due to size constraints. The exception message provides details about the specific issue
 * with the file size.
 *
 * <p>The HTTP status is set to 400 (Bad Request) to indicate that the request could not be
 * processed due to invalid input, specifically the size of the file.
 */
public class FileSizeExceededException extends HttpException {

  private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

  public FileSizeExceededException(String message) {
    super(message, STATUS);
  }
}
