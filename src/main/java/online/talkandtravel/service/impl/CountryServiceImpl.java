package online.talkandtravel.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.service.CountryService;
import online.talkandtravel.util.mapper.CountryMapper;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

  private final CountryRepository countryRepository;
  private final UserCountryRepository userCountryRepository;
  private final CountryMapper countryMapper;

  @Override
  public List<CountryInfoDto> getAllCountriesInfo() {
    return countryRepository.findAll().stream().map(countryMapper::toCountryInfoDto).toList();
  }

  @Override
  public CountryDto findCountryByName(String countryName) {
    Country country = getCountry(countryName);
    return countryMapper.toCountryDto(country);
  }

  @Override
  public List<CountryInfoDto> findAllCountriesByUserId(Long userId) {
    List<UserCountry> userCountries = userCountryRepository.findByUserId(userId);

    return userCountries.stream().map(countryMapper::toCountryInfoDto).toList();
  }

  private Country getCountry(String countryName) {
    return countryRepository
        .findById(countryName)
        .orElseThrow(() -> new CountryNotFoundException(countryName));
  }
}
