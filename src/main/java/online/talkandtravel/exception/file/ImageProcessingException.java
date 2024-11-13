package online.talkandtravel.exception.file;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an image cannot be processed due to semantic issues or unsupported content.
 */
public class ImageProcessingException extends HttpException {

  private static final String MESSAGE = "Failed to process avatar image: %s";

  private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

  public ImageProcessingException(String message) {
    super(MESSAGE.formatted(message), STATUS);
  }

  public ImageProcessingException(String message, String messageToClient) {
    super(MESSAGE.formatted(message), messageToClient, STATUS);
  }
}
