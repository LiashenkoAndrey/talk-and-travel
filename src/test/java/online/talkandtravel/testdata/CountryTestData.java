package online.talkandtravel.testdata;

import java.util.List;
import online.talkandtravel.model.dto.country.CountryInfoDto;

public final class CountryTestData {

  private static final int COUNTRIES_COUNT = 240;

  private CountryTestData() {
    throw new UnsupportedOperationException(
        "Utility CountryTestData class cannot be instantiated");
  }

  public static int getCountriesCount(){
    return COUNTRIES_COUNT;
  }

  public static List<CountryInfoDto> getUser1Countries(){
    return List.of(
        new CountryInfoDto("United States of America", "us"),
        new CountryInfoDto("Canada", "ca"),
        new CountryInfoDto("France", "fr"),
        new CountryInfoDto("Germany", "de"),
        new CountryInfoDto("Japan", "jp")
    );
  }
}
