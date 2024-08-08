package online.talkandtravel.exception.user;

import online.talkandtravel.exception.model.ApiException;

public class UserCountryNotFoundException extends ApiException {

  private static final String MESSAGE =
      "UserCountry connection not exists for user %s and Country: %s";

  public UserCountryNotFoundException(String name, Long id) {
    super(String.format(MESSAGE, id, name));
  }
}
