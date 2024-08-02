package online.talkandtravel.util.mapper;

import online.talkandtravel.model.Country;
import online.talkandtravel.model.dto.CountryDto;
import online.talkandtravel.model.dto.OpenCountryRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryDtoMapper {
    CountryDto mapToDto(Country country);

    Country mapToModel(OpenCountryRequestDto dto);

    Country mapToModel(CountryDto dto);
}
