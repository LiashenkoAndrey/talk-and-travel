package online.talkandtravel.service;

import online.talkandtravel.model.Country;
import online.talkandtravel.model.dto.CountryWithUserDto;

import java.util.List;

public interface CountryService {

    Country create(Country country, Long userID);

    Country update(Long countryId, Long userID);

    Country save(Country country);

    Country findById(Long countryId);

    Country findByName(String countryMame);

    List<Country> getAll();

    Long countUsersInCountry(Long countryId);

    List<Country> findAllCountriesByUser(Long userId);

    CountryWithUserDto findByIdWithParticipants(Long countryId);
}
