package online.talkandtravel.service.impl;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.UserAlreadySubscribedException;
import online.talkandtravel.model.Country;
import online.talkandtravel.repository.CountryRepo;
import online.talkandtravel.util.CountryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j2
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountryServiceImplTest {}/*

    private final String countryName = "Albania";

    @Autowired
    private CountryRepo countryRepo;

    @Autowired
    private CountryServiceImpl service;

    @Autowired
    private CountryUtil util;

    private Country country;

    @BeforeEach
    public void setUp()  {
        country = util.createCountry(countryName, "al");
    }
    @Test
    void joinUserToCountry() {
        assertTrue(countryRepo.existsById(country.getId()));
        assertFalse(countryRepo.isUserSubscribed(countryName,1L));

        service.joinUserToCountry(1L, countryName);
        assertTrue(countryRepo.isUserSubscribed(countryName,1L));
        assertThrows(UserAlreadySubscribedException.class, () -> service.joinUserToCountry(1L, countryName));
    }
}*/