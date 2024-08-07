package online.talkandtravel.util.dataManager;

import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;

public interface DataManager {

  void checkAndPopulateCountries();

  void checkAndCreateChats();

  void addAdmin();
}
