package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.service.impl.CountryServiceImpl;
import online.talkandtravel.util.mapper.CountryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CountryServiceImplTest {
  @Mock private CountryRepository countryRepository;
  @Mock private UserCountryRepository userCountryRepository;
  @Mock private CountryMapper countryMapper;

  @InjectMocks private CountryServiceImpl underTest;

  private Country country1;
  private Country country2;
  private CountryDto countryDto;
  private CountryInfoDto countryInfoDto1;
  private CountryInfoDto countryInfoDto2;

  private List<UserCountry> userCountries;
  private List<CountryInfoDto> countryInfoDtos;

  @BeforeEach
  void setUp() {
    country1 = new Country();
    country1.setName("Country1");

    country2 = new Country();
    country2.setName("Country2");

    UserCountry userCountry1 = new UserCountry();
    userCountry1.setCountry(country1);
    UserCountry userCountry2 = new UserCountry();
    userCountry2.setCountry(country2);

    userCountries = List.of(userCountry1, userCountry2);

    countryInfoDto1 = CountryInfoDto.builder().name("Country1").flagCode("FlagCode1").build();

    countryInfoDto2 = CountryInfoDto.builder().name("Country2").flagCode("FlagCode2").build();

    countryInfoDtos = List.of(countryInfoDto1, countryInfoDto2);

    countryDto = new CountryDto(
        "Country1",
        "FlagCode1",
        Collections.emptyList() // Empty list of chats
    );
  }

  @Test
  void getAllCountriesInfo_shouldReturnListOfCountryInfoDto() {
    List<Country> countries = List.of(country1, country2);
    when(countryRepository.findAll()).thenReturn(countries);
    when(countryMapper.toCountryInfoDto(country1)).thenReturn(countryInfoDto1);
    when(countryMapper.toCountryInfoDto(country2)).thenReturn(countryInfoDto2);

    List<CountryInfoDto> result = underTest.getAllCountriesInfo();

    assertEquals(2, result.size());
    assertEquals(countryInfoDto1, result.get(0));
    assertEquals(countryInfoDto2, result.get(1));
    verify(countryRepository, times(1)).findAll();
    verify(countryMapper, times(1)).toCountryInfoDto(country1);
    verify(countryMapper, times(1)).toCountryInfoDto(country2);
  }

  @Test
  void getAllCountriesInfo_shouldReturnEmptyList_whenNoCountriesFound() {
    when(countryRepository.findAll()).thenReturn(List.of());

    List<CountryInfoDto> result = underTest.getAllCountriesInfo();

    assertEquals(0, result.size());
    verify(countryRepository, times(1)).findAll();
    verifyNoInteractions(countryMapper); // Ensure countryMapper is not called
  }

  @Test
  void findCountryByName_shouldReturnCountryDto_whenCountryExists() {
    when(countryRepository.findById("Country1")).thenReturn(Optional.of(country1));
    when(countryMapper.toCountryDto(country1)).thenReturn(countryDto);

    CountryDto result = underTest.findCountryByName("Country1");

    assertEquals(countryDto, result);
    verify(countryRepository, times(1)).findById("Country1");
    verify(countryMapper, times(1)).toCountryDto(country1);
  }

  @Test
  void findCountryByName_shouldThrowCountryNotFoundException_whenCountryDoesNotExist() {
    when(countryRepository.findById("NonExistentCountry")).thenReturn(Optional.empty());

    assertThrows(CountryNotFoundException.class, () -> underTest.findCountryByName("NonExistentCountry"));

    verify(countryRepository, times(1)).findById("NonExistentCountry");
    verifyNoInteractions(countryMapper);
  }

  @Test
  void findAllCountriesByUserId_shouldReturnCountryInfoDtos_whenUserCountriesExist() {
    when(userCountryRepository.findByUserId(1L)).thenReturn(userCountries);
    when(countryMapper.userCountryToCountryInfoDto(userCountries.get(0)))
        .thenReturn(countryInfoDtos.get(0));
    when(countryMapper.userCountryToCountryInfoDto(userCountries.get(1)))
        .thenReturn(countryInfoDtos.get(1));

    List<CountryInfoDto> result = underTest.findAllCountriesByUserId(1L);

    assertEquals(countryInfoDtos, result);
    verify(userCountryRepository, times(1)).findByUserId(1L);
    verify(countryMapper, times(1)).userCountryToCountryInfoDto(userCountries.get(0));
    verify(countryMapper, times(1)).userCountryToCountryInfoDto(userCountries.get(1));
  }

  @Test
  void findAllCountriesByUserId_shouldReturnEmptyList_whenNoUserCountriesExist() {
    when(userCountryRepository.findByUserId(2L)).thenReturn(List.of());

    List<CountryInfoDto> result = underTest.findAllCountriesByUserId(2L);

    assertEquals(List.of(), result);
    verify(userCountryRepository, times(1)).findByUserId(2L);
    verifyNoInteractions(countryMapper);
  }

}
