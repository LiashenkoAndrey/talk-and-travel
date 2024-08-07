package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.dto.OpenCountryRequestDto;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CountryMapper {

  CountryInfoDto toCountryInfoDto(Country country);
}
