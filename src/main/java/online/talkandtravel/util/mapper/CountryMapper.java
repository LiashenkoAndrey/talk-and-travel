package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.entity.Country;
import org.mapstruct.Mapper;

@Mapper(
    config = MapperConfig.class,
    uses = {
      ChatMapper.class,
      UserMapper.class,
      EventMapper.class,
      MessageMapper.class,
      UserChatMapper.class
    })
public interface CountryMapper {

  CountryInfoDto toCountryInfoDto(Country country);

  CountryDto toCountryDto(Country country);
}
