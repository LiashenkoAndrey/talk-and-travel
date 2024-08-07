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


      @Operation(
              description = "Get the quantity of participants in the country."
      )
      @GetMapping("/user-count/{countryId}")
      public ResponseEntity<Long> countUsersInCountry(@PathVariable Long countryId) {
          var usersInCountry = countryService.countUsersInCountry(countryId);
          return ResponseEntity.ok().body(usersInCountry);
      }

      @Operation(
              description = "Find all countries where the user is a participant"
      )
      @GetMapping("/all-by-user/{userId}/participating")
      public ResponseEntity<List<CountryInfoDto>> findCountriesByUserId(@PathVariable Long userId) {
          List<Country> countriesByUserId = countryService.findAllCountriesByUser(userId);
          List<CountryInfoDto> responseCountryDtos
                  = countriesByUserId.stream()
                  .map(countryDtoMapper::mapToDto)
                  .toList();
          return ResponseEntity.ok().body(responseCountryDtos);
      }

      @Operation(
              description = "Get Country by ID with users instead of participants."
      )
      @GetMapping("/{countryId}/with-users")
      public ResponseEntity<CountryWithUserDto> findByIdWithUsers(@PathVariable Long countryId) {
          CountryWithUserDto countryWithUserDto = countryService.findByIdWithParticipants(countryId);
          return ResponseEntity.ok().body(countryWithUserDto);
      }

      @Operation(
              description = "Get Country by ID with users instead of participants."
      )
      @GetMapping("/{countryId}/participants")
      public List<IParticipantDto> findParticipantsByChatId(@PathVariable Long countryId) {
          List<IParticipantDto> participants = countryRepo.findAllParticipantsByChatId(countryId);
          log.info("findParticipantsByChatId participants size - {}", participants.size());
          log.info("findParticipantsByChatId participants - {}", participants);
          return participants;
      }
  }
  */
