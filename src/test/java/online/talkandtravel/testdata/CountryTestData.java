package online.talkandtravel.testdata;

import java.util.List;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.dto.country.CountryInfoWithUnreadMessagesDto;

public final class CountryTestData {

  private static final int COUNTRIES_COUNT = 240;

  private CountryTestData() {
    throw new UnsupportedOperationException(
        "Utility CountryTestData class cannot be instantiated");
  }

  public static int getCountriesCount(){
    return COUNTRIES_COUNT;
  }

  public static List<CountryInfoWithUnreadMessagesDto> getUser1Countries(){
    return List.of(
        new CountryInfoWithUnreadMessagesDto("United States of America", "us", 0L),
        new CountryInfoWithUnreadMessagesDto("Canada", "ca", 0L),
        new CountryInfoWithUnreadMessagesDto("France", "fr", 0L),
        new CountryInfoWithUnreadMessagesDto("Germany", "de", 0L),
        new CountryInfoWithUnreadMessagesDto("Japan", "jp", 0L)
    );
  }
}
