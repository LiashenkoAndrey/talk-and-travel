package online.talkandtravel.service;

import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;

import java.util.List;

public interface CountryService {

  List<CountryInfoDto> getAllCountriesInfo();

  CountryDto findCountryByName(String countryName);

  List<CountryInfoDto> findAllCountriesByUserId(Long userId);

  /*boolean userIsSubscribed(String countryName, Long userId);

      Country createAndSave(Country country);

      void joinUserToCountry(Long userId, String countryName);

      Country update(Long countryId, Long userID);

      Country save(Country country);

      Country findById(Long countryId);

      CountryDtoWithParticipantsAmountAndMessages findByNameAndCreateIfNotExist(String name, OpenCountryRequestDto requestDto);

  //    Country findByName(String countryMame);



      Long countUsersInCountry(Long countryId);

      List<Country> findAllCountriesByUser(Long userId);

      CountryWithUserDto findByIdWithParticipants(Long countryId);

      void addNewParticipantToCountry(NewParticipantCountryDto dto);*/
}
