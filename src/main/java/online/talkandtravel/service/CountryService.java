package online.talkandtravel.service;

import java.util.List;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.dto.country.CountryInfoWithUnreadMessagesDto;

/**
 * Service interface for managing country-related operations.
 *
 * <p>This service provides methods to interact with country data, including retrieving country
 * information, finding countries by name, and obtaining country details associated with a user.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #getAllCountriesInfo()} - Retrieves a list of information for all countries. The
 *       method returns a {@link List} of {@link CountryInfoDto} objects, each representing detailed
 *       information about a country.
 *   <li>{@link #findCountryByName(String)} - Finds a country based on its name. The method returns
 *       a {@link CountryDto} representing the country associated with the specified name.
 *   <li>{@link #findAllUserCountries()} - Retrieves a list of country information
 *       associated with a specific user. The method returns a {@link List} of {@link
 *       CountryInfoDto} objects representing countries that are related to the user identified by
 *       the provided user ID.
 * </ul>
 */
public interface CountryService {

  List<CountryInfoDto> getAllCountriesInfo();

  CountryDto findCountryByName(String countryName);

  List<CountryInfoWithUnreadMessagesDto> findAllUserCountries();
}
