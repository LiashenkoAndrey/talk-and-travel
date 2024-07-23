package online.talkandtravel.util.mapper;

import online.talkandtravel.model.Country;
import online.talkandtravel.model.dto.CountryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryDtoMapper {
    CountryDto mapToDto(Country country);

    Country mapToModel(CountryDto dto);
}
