package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.UserCountry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

  @Mapping(target = "name", source = "country.name")
  @Mapping(target = "flagCode", source = "country.flagCode")
  CountryInfoDto toCountryInfoDto(UserCountry userCountry);

  CountryDto toCountryDto(Country country);
}
