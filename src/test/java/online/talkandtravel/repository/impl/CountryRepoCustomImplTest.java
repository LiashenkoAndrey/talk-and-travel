package online.talkandtravel.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.Country;
import online.talkandtravel.model.GroupMessage;
import online.talkandtravel.model.Participant;
import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.CountryDtoWithParticipantsAmountAndMessages;
import online.talkandtravel.repository.CountryRepo;
import online.talkandtravel.repository.GroupMessageRepository;
import online.talkandtravel.repository.ParticipantRepository;
import online.talkandtravel.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j2
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountryRepoCustomImplTest {

    private final String countryName = "Albania";

    @Autowired
    private CountryRepoCustomImpl repoCustom;

    @Autowired
    private CountryRepo countryRepo;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ParticipantRepository participantRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    public void setUp()  {
        saveEntities();
    }

    @Test
    void findByNameAndCreateIfNotExist() {
        CountryDtoWithParticipantsAmountAndMessages dto = repoCustom.findDtoByName(countryName);
        assertNotNull(dto);
        assertEquals(1L, dto.getParticipantsAmount());
        assertEquals(2L, dto.getGroupMessages().size());
    }

    public void saveEntities() {
        User user = userRepo.getReferenceById(1L);
        Country country = countryRepo.save(Country.builder()
                .name(countryName)
                .flagCode("fc")
                .build());
        saveParticipant(user, country.getId());
        groupMessageRepository.saveAll(List.of(
                new GroupMessage("HelloWorld", country, user),
                new GroupMessage("I'm a test message!", country, user)
        ));
    }

    private void saveParticipant(User user, Long countryId) {
        Participant participant = participantRepository.save(new Participant(user));

        em.createNativeQuery("insert into public.participant_countries(country_id, participant_id) values (:country_id, :participant_id)")
                .setParameter("country_id", countryId)
                .setParameter("participant_id", participant.getId())
                .executeUpdate();
    }
}