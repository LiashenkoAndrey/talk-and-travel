package online.talkandtravel.util.fillCountryTable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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

  @PersistenceContext
  private EntityManager em;

  @Value("${COUNTRIES_JSON_FILE_PATH}")
  private String jsonFilePath;

  @Qualifier("CountryArraySelectorImpl_v1")
  @Autowired
  private CountryArraySelector countryArraySelector;

  private List<CountryWithNameAndFlag> list = new ArrayList<>();

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

  private List<CountryWithNameAndFlag> toCountryList(JsonNode jsonArray) {
    log.debug("iterate array...");
    for (JsonNode node : jsonArray) {

      CountryWithNameAndFlag country = countryArraySelector.selectCountry(node);

      if (listHasCollisionWith(country)) {
        country.setFlagCode(country.getFlagCode() + "-collision-" + UUID.randomUUID());
        list.add(country);
        log.debug("collision {}", country);
      } else {
        list.add(country);

      }
    }
    log.debug("iterate ok");
    return list;
  }

  @Transactional
  public void saveAll(List<CountryWithNameAndFlag> list) {
    log.debug("save...");
    list.stream().distinct().forEach((country -> {
      em.persist(country);
    }));
    log.debug("save ok");
  }

  private boolean listHasCollisionWith(CountryWithNameAndFlag country)  {
    return !list.stream().noneMatch(c -> c.getFlagCode().equals(country.getFlagCode()));
  }

  private boolean isTableEmpty() {
    return em.createQuery("from Country c", CountryWithNameAndFlag.class)
        .setMaxResults(1)
        .getResultList()
        .isEmpty();
  }
}
