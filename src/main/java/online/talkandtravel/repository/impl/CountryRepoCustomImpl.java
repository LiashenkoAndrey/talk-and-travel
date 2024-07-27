package online.talkandtravel.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.CountryDtoWithParticipantsAmountAndMessages;
import online.talkandtravel.model.dto.MessageDto;
import online.talkandtravel.repository.CountryRepoCustom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Log4j2
public class CountryRepoCustomImpl implements CountryRepoCustom {

    @PersistenceContext
    private EntityManager em;

    /**
     * Gets a country DTO with messages and participants amount
     * @param name name of country
     * @return dto
     */
    @Override
    public CountryDtoWithParticipantsAmountAndMessages findDtoByName(String name) {
        CountryDtoWithParticipantsAmountAndMessages countryDto = getCountryDto(name);
        Long participantsAmount = getParticipantsAmount(countryDto.getId());
        List<MessageDto> messagesDto = getMessagesDto(name);

        countryDto.setParticipantsAmount(participantsAmount);
        countryDto.setGroupMessages(messagesDto);
        return countryDto;
    }

    private CountryDtoWithParticipantsAmountAndMessages getCountryDto(String countryName) {
        return em.createQuery("select c.id as id , c.name as name, c.flagCode as flagCode from Country c where c.name = :name", CountryDtoWithParticipantsAmountAndMessages.class)
                .setParameter("name", countryName)
                .getSingleResult();

    }

    private Long getParticipantsAmount(Long countryId) {
        return (Long) em.createNativeQuery("select count(*) from public.participant_countries pc where pc.country_id = :country_id")
                .setParameter("country_id", countryId)
                .getSingleResult();
    }

    private List<MessageDto> getMessagesDto(String countryName) {
        return em.createQuery("""
                        select m.id as id, m.content as content, m.creationDate as creationDate, new online.talkandtravel.model.dto.UserDtoBasic(m.user.id, m.user.userName, m.user.userEmail, m.user.about) as user  
                        from GroupMessage m 
                        where m.country.name = :name 
                        order by m.creationDate desc 
                        """, MessageDto.class)
                .setParameter("name", countryName)
                .getResultList();
    }
}
