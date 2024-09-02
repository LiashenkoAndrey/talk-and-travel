package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.USER_COUNTRIES_DATA_SQL;
import static online.talkandtravel.testdata.CountryTestData.getCountriesCount;
import static online.talkandtravel.testdata.CountryTestData.getUser1Countries;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.enums.Role;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.CountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {USER_COUNTRIES_DATA_SQL})
public class CountryServiceIntegrationTest extends IntegrationTest {

  @Autowired
  private CountryService underTest;

  @Autowired
  private AuthenticationService authenticationService;


  @Test
  void getAllCountriesInfo_shouldReturnAllCountriesInfo() {
    int expectedCount = getCountriesCount();

    List<CountryInfoDto> allCountriesInfo = underTest.getAllCountriesInfo();

    assertThat(allCountriesInfo.size()).isEqualTo(expectedCount);
  }

  @Test
  void findAllCountriesByUserId_shouldReturnAllCountriesInfo() {
    User user = User.builder()
        .id(1L)
        .role(Role.USER)
        .build();
    UserDetails userDetails = new CustomUserDetails(user);
    authenticationService.authenticateUser(userDetails, null);

    List<CountryInfoDto> expected = getUser1Countries();

    List<CountryInfoDto> actual = underTest.findAllUserCountries();

    assertThat(actual.size()).isEqualTo(expected.size());
    assertThat(actual).isEqualTo(expected);
  }
}
