package online.talkandtravel.service;

import online.talkandtravel.model.Country;
import online.talkandtravel.model.dto.CountryDtoWithParticipantsAmountAndMessages;
import online.talkandtravel.model.dto.CountryWithUserDto;
import online.talkandtravel.model.dto.NewParticipantCountryDto;
import online.talkandtravel.model.dto.OpenCountryRequestDto;

import java.util.List;

public interface CountryService {

    boolean userIsSubscribed(String countryName, Long userId);

    Country createAndSave(Country country);

    void joinUserToCountry(Long userId, String countryName);

    Country update(Long countryId, Long userID);

    Country save(Country country);

    Country findById(Long countryId);

    CountryDtoWithParticipantsAmountAndMessages findByNameAndCreateIfNotExist(String name, OpenCountryRequestDto requestDto);

//    Country findByName(String countryMame);

    List<Country> getAll();

    Long countUsersInCountry(Long countryId);

    List<Country> findAllCountriesByUser(Long userId);

    CountryWithUserDto findByIdWithParticipants(Long countryId);

    void addNewParticipantToCountry(NewParticipantCountryDto dto);
}
