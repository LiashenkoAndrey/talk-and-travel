package online.talkandtravel.util.dataManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.data.FailedToReadJsonException;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Selects data from any json file and inserts it in a countries table */
@Component
@Log4j2
@RequiredArgsConstructor
public class TableDataManager implements DataManager {

  @Value("${COUNTRIES_JSON_FILE_PATH}")
  private String jsonFilePath;

  @Value("${USER_ADMIN_NAME}")
  private String adminName;

  @Value("${USER_ADMIN_EMAIL}")
  private String adminEmail;

  @Value("${USER_ADMIN_PASSWORD}")
  private String adminPassword;

  private final CountryRepository countryRepository;

  private final ChatRepository chatRepository;

  private final UserService userService;

  private final CountryArraySelector countryArraySelector;

  private List<Country> list = new ArrayList<>();

  @Override
  @Transactional
  public void checkAndPopulateCountries() {
    if (isTableEmpty()) {
      JsonNode readJson;
      try {
        readJson = readJson();
      } catch (IOException e) {
        throw new FailedToReadJsonException(jsonFilePath);
      }
      JsonNode jsonArray = countryArraySelector.selectCountryArray(readJson);
      list = toCountryList(jsonArray);
      saveAll(list);
    } else {
      log.debug("country db table isEmpty - {}", isTableEmpty());
    }
  }

  @Transactional
  public void saveAll(List<Country> list) {
    log.debug("save...");
    countryRepository.saveAll(list);
    log.debug("save ok");
  }

  @Override
  @Transactional
  public void checkAndCreateChats() {
    if (chatRepository.countChats() == 0) {
      log.debug("Chats table is Empty. Creating country chats...");
      createInitialChats();
    } else {
      log.debug("Chats table already exists. No new chats will be created.");
    }
  }

  @Override
  public void addAdmin() {
    userService
        .findUserByEmail(adminEmail)
        .ifPresentOrElse(
            user -> {},
            () -> {
              try {
                userService.save(
                    User.builder()
                        .userName(adminName)
                        .userEmail(adminEmail)
                        .password(adminPassword)
                        .role(Role.ADMIN)
                        .build());
              } catch (IOException e) {
                throw new RuntimeException("Cant generate standard avatar.");
              }
            });
  }

  private void createInitialChats() {
    List<Country> allCountries = countryRepository.findAll();
    allCountries.forEach(
        country -> {
          Chat chat =
              Chat.builder()
                  .name(country.getName())
                  .chatType(ChatType.GROUP)
                  .description(country.getName() + " main chat")
                  .build();
          country.getChats().add(chat);
        });
    countryRepository.saveAll(allCountries);
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

  private boolean isTableEmpty() {
    return countryRepository.countCountries() == 0;
  }
}
