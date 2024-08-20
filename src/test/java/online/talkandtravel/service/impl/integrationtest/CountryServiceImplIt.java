package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.USER_COUNTRIES_DATA_SQL;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.service.CountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = {USER_COUNTRIES_DATA_SQL})
public class CountryServiceImplIt extends IntegrationTest {

  @Autowired
  private CountryService underTest;

  @Test
  void getAllCountriesInfo_shouldReturnAllCountriesInfo() {
    List<CountryInfoDto> allCountriesInfo = underTest.getAllCountriesInfo();
    assertThat(allCountriesInfo.size()).isEqualTo(240);
  }

  @Test
  void findAllCountriesByUserId_shouldReturnAllCountriesInfo() {
    Long userId = 1L;
    List<CountryInfoDto> allCountriesByUserId = underTest.findAllCountriesByUserId(userId);
    assertThat(allCountriesByUserId.size()).isEqualTo(5);
  }
}
