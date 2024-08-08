package online.talkandtravel.service;

import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;

import java.util.List;

public interface CountryService {

  List<CountryInfoDto> getAllCountriesInfo();

  CountryDto findCountryByName(String countryName);

  List<CountryInfoDto> findAllCountriesByUserId(Long userId);
}
