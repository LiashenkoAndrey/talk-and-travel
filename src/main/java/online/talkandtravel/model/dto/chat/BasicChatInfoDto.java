package online.talkandtravel.model.dto.chat;

import online.talkandtravel.model.dto.country.CountryInfoDto;

public record BasicChatInfoDto (
    Long id,
    String name,
    Long usersCount,
    CountryInfoDto country
) {

}