package online.talkandtravel.util.createCountryChats;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.service.CountryService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CountryChatManager {

  private final ChatService chatService;
  private final CountryService countryService;

  public void checkAndCreateChats() {
    if (chatService.countAllChats() == 0) {
      log.debug("Chats table is Empty. Creating country chats...");
      countryService.createInitialChats();
    } else {
      log.debug("Chats table already exists. No new chats will be created.");
    }
  }


}
