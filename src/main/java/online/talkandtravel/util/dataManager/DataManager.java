package online.talkandtravel.util.dataManager;

/**
 * Defines the operations for managing and populating data in the application.
 *
 * <p>The {@code DataManager} interface outlines methods for checking and populating countries,
 * creating initial chat entries, and adding an admin user. Implementations of this interface handle
 * data initialization and ensure that necessary data is present in the system, performing these
 * actions only when required.
 *
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #checkAndPopulateCountries()} - Checks if the countries table is empty and populates
 *       it with data if necessary.
 *   <li>{@link #checkAndCreateChats()} - Checks if the chat table is empty and creates initial chat
 *       entries if needed.
 *   <li>{@link #addAdmin()} - Adds an admin user to the system if one does not already exist.
 * </ul>
 *
 * <p>Implementations of this interface will typically interact with repositories and services to
 * perform data operations and ensure the application is properly initialized.
 *
 * @see TableDataManager
 */
public interface DataManager {

  void prepareRedisData();

  void checkAndPopulateCountries();

  void checkAndCreateChats();

  void addAdmin();
}
