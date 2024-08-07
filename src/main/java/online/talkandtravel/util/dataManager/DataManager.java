package online.talkandtravel.util.dataManager;

import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;

public interface DataManager {

  void checkAndPopulateCountries() throws IOException;

  void checkAndCreateChats();

  void addAdmin();
}
