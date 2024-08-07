package online.talkandtravel.exception.country;

import online.talkandtravel.exception.model.ApiException;

public class CountryNotFoundException extends ApiException {

  private static final String MESSAGE = "Country [%s] not found";

  public CountryNotFoundException(String country) {
    super(MESSAGE.formatted(country));
  }
}
