package online.talkandtravel.util.mapper;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.country.CountryDto;
import online.talkandtravel.model.dto.country.CountryInfoDto;
import online.talkandtravel.model.dto.country.CountryInfoWithUnreadMessagesDto;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.UserCountry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link Country} entities and {@link CountryDto} and
 * {@link CountryInfoDto} data transfer objects.
 *
 * <p>This interface uses MapStruct to define methods that map properties between {@link Country}
 * and {@link UserCountry} entities and their corresponding DTO representations. It handles the
 * conversion of basic country information as well as information related to user countries.
 *
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #toCountryInfoDto(Country)} - Converts a {@link Country} entity to a {@link
 *       CountryInfoDto}. This method maps the basic properties of the {@link Country} entity to the
 *       DTO.
 *   <li>{@link #toCountryDto(Country)} - Converts a {@link Country} entity to a {@link CountryDto}.
 *       This method maps the properties of the {@link Country} entity to the DTO, potentially
 *       including additional information.
 * </ul>
 *
 * <p>This mapper relies on other mappers for converting nested objects or related data, such as
 * {@link ChatMapper}, {@link UserMapper}, {@link MessageMapper}, and {@link
 * UserChatMapper}. It is configured with {@link MapperConfig} to apply global mapping settings.
 *
 */
@Mapper(
    config = MapperConfig.class,
    uses = {
      ChatMapper.class,
      UserMapper.class,
      MessageMapper.class,
      UserChatMapper.class
    })
public interface CountryMapper {

  CountryInfoDto toCountryInfoDto(Country country);

  CountryDto toCountryDto(Country country);
}