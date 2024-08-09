package online.talkandtravel.exception.file;

import online.talkandtravel.exception.model.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an image cannot be processed due to semantic issues or unsupported content.
 */
public class ImageProcessingException extends ApiException {

  private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

  public ImageProcessingException(String message) {
    super(message, STATUS);
  }
}
