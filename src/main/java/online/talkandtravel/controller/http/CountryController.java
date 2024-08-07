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
  public ResponseEntity<CountryDto> findCountryById(@PathVariable("country") String country){
    return ResponseEntity.ok(countryService.findCountryByName(country));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<CountryInfoDto>> getAllCountriesByUserId(@PathVariable("userId") Long userId){
    return ResponseEntity.ok(countryService.findAllCountriesByUserId(userId));
  }

} /*

      @Operation(
              description = "Joins a user to a country"
      )
      @PostMapping("/{countryName}/join")
      public ResponseEntity<?> joinUserToCountry(@RequestBody Long userId, @PathVariable String countryName) {
          log.info("join country userId - {}, countryName - {}", userId, countryName);
          countryService.joinUserToCountry(userId, countryName);
          return ResponseEntity.ok().build();
      }


  }
  */
