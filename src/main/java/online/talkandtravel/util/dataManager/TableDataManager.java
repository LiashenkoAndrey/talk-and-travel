package online.talkandtravel.util.dataManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages data population and initialization for countries, chats, and admin users.
 *
 * <p>This component handles reading data from JSON files, populating country data into the
 * database, creating initial chat entries based on the countries, and adding an admin user if not
 * already present. The class ensures that data is only populated when necessary, avoiding duplicate
 * entries.
 *
 * <p>Key functionalities include:
 *
 * <ul>
 *   <li>Reading and parsing JSON files containing country data.
 *   <li>Saving country data into the database if the country table is empty.
 *   <li>Creating initial chat entries if the chat table is empty.
 *   <li>Adding an admin user if one does not already exist.
 * </ul>
 */
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

  private final RedisTemplate<String, String> redisTemplate;

  private final UserRepository userRepository;

  private List<Country> list = new ArrayList<>();

  @Override
  public void prepareRedisData() {
    Map<String, String> lastSeenUsersData = userRepository.findAll().stream()
        .collect(Collectors.toMap(
            (user) -> RedisUtils.getUserLastSeenKey(user.getId()),
            this::getLastSeenOnFromUser));

    redisTemplate.opsForValue().multiSet(lastSeenUsersData);
  }

  private String getLastSeenOnFromUser(User user) {
    Optional<LocalDateTime> lastSeenOn = Optional.ofNullable(user.getLastLoggedOn());
    return lastSeenOn.map(LocalDateTime::toString)
        .orElseGet(() -> {
          log.warn("user with id: {} has lastSeenOn field as null", user);
          return "null";
        });
  }

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
    log.debug("Saving countries...");
    countryRepository.saveAll(list);
    log.debug("Countries saved.");
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
            () ->
                userService.save(
                    User.builder()
                        .userName(adminName)
                        .userEmail(adminEmail)
                        .password(adminPassword)
                        .role(Role.ADMIN)
                        .build()));
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
    for (JsonNode node : jsonArray) {
      Country country = countryArraySelector.selectCountry(node);
      list.add(country);
    }
    return list;
  }

  private boolean isTableEmpty() {
    return countryRepository.countCountries() == 0;
  }
}
