package online.talkandtravel.exception.attachment;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class MultipartFileIsEmptyException extends HttpException {

  private static final String MESSAGE = "Provided multipart file is empty. Property name - 'file'";
  private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

  public MultipartFileIsEmptyException() {
    super(MESSAGE, STATUS);
  }
}
