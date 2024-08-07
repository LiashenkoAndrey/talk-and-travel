package online.talkandtravel.repository;

import online.talkandtravel.model.dto.country.CountryDtoWithParticipantsAmountAndMessages;

public interface CountryRepoCustom {

    /**
     * Gets a country DTO with messages and participants amount
     * @param name name of country
     * @return dto
     */
    CountryDtoWithParticipantsAmountAndMessages findDtoByName(String name);
}
