package online.talkandtravel.controller.http;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.service.CountryService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class responsible for managing country-related operations through HTTP endpoints.
 *
 * <p>This controller offers a variety of methods to retrieve and manipulate data related to countries:</p>
 *
 * <ul>
 *   <li>{@code getAllCountriesInfo} - Retrieves a list of basic information for all available countries.</li>
 *   <li>{@code findCountryById} - Finds and returns detailed information about a specific country based on its name.</li>
 *   <li>{@code getAllCountriesByUserId} - Returns a list of countries associated with a specific user, identified by their user ID.</li>
 * </ul>
 *
 * <p>The {@link CountryService} handles the underlying business logic, and responses are encapsulated in DTOs to ensure
 * a consistent and structured format for the API clients.</p>
 */


@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/countries")
@RequiredArgsConstructor
@Log4j2
public class CountryController {

  private final CountryService countryService;

  @GetMapping("/info")
  public ResponseEntity<List<CountryInfoDto>> getAllCountriesInfo() {
    return ResponseEntity.ok(countryService.getAllCountriesInfo());
  }

  @GetMapping("/{country}")
  public ResponseEntity<CountryDto> findCountryById(@PathVariable String country) {
    return ResponseEntity.ok(countryService.findCountryByName(country));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<CountryInfoDto>> getAllCountriesByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(countryService.findAllCountriesByUserId(userId));
  }
}
