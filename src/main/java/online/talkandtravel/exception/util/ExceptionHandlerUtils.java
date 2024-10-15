package online.talkandtravel.exception.util;

import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;

public class ExceptionHandlerUtils {

  public static final String VALIDATION_FAILED_MESSAGE = "Validation failed: ";


  /**
   * Extracts and formats validation errors from the {@link BindingResult}.
   *
   * @param bindingResult the {@link BindingResult} containing validation errors
   * @return a formatted string of validation errors, with each error separated by a comma
   */
  public static String getArgumentValidations(BindingResult bindingResult) {
    return bindingResult.getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
        .collect(Collectors.joining(", "));
  }

}
