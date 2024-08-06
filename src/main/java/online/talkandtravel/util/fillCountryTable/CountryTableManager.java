package online.talkandtravel.util.fillCountryTable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Selects data from any json file and inserts it in a countries table
 */
@Component
@Log4j2
public class CountryTableManager {

  public CountryTableManager(@Value("${COUNTRIES_JSON_FILE_PATH}") String jsonFilePath,
      @Qualifier("CountryArraySelectorImpl_v1") CountryArraySelector countryArraySelector,
      List<Country> list, CountryRepository countryRepository) {
    this.jsonFilePath = jsonFilePath;
    this.countryArraySelector = countryArraySelector;
    this.list = list;
    this.countryRepository = countryRepository;
  }

  private final String jsonFilePath;

  private final CountryRepository countryRepository;

  private final CountryArraySelector countryArraySelector;

  private List<Country> list;

  @Transactional
  public void readJsonAndSaveAllIfTableIsEmpty() throws IOException {
    if (isTableEmpty()) {
      JsonNode readJson = readJson();
      JsonNode jsonArray = countryArraySelector.selectCountryArray(readJson);
      list = toCountryList(jsonArray);
      saveAll(list);
    } else {
      log.debug("country db table isEmpty - {}", isTableEmpty());
    }
  }

  private JsonNode readJson() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(new File(jsonFilePath));
  }

  private List<Country> toCountryList(JsonNode jsonArray) {
    log.debug("iterate an array...");
    for (JsonNode node : jsonArray) {

      Country country = countryArraySelector.selectCountry(node);

      list.add(country);
    }
    log.debug("iterate ok");
    return list;
  }

  @Transactional
  public void saveAll(List<Country> list) {
    log.debug("save...");
    countryRepository.saveAll(list);
    log.debug("save ok");
  }

  private boolean listHasCollisionWith(Country country)  {
    return list.stream().anyMatch(c -> c.getFlagCode().equals(country.getFlagCode()));
  }

  private boolean isTableEmpty() {
    return countryRepository.countCountries() == 0;
  }
}
