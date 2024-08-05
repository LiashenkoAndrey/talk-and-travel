package online.talkandtravel.repository.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j2
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountryRepositoryCustomImplTest {}/*
    private final String countryName = "Albania";

    @Autowired
    private CountryRepoCustomImpl repoCustom;

    @Autowired
    private CountryRepository countryRepo;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CountryUtil util;

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
        Country country = util.createCountry(countryName, "fc");
        util.saveParticipant(user, country.getId());
        groupMessageRepository.saveAll(List.of(
                new GroupMessage("HelloWorld", country, user),
                new GroupMessage("I'm a test message!", country, user)
        ));
    }
}*/