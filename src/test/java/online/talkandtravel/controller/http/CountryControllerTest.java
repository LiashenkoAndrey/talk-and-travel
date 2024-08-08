package online.talkandtravel.controller.http;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@RunWith(SpringRunner.class)
@Log4j2
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CountryControllerTest {}/*

    @Autowired
    private MockMvc mockMvc;

    private final String countryName = "Albania";

    @Autowired
    private CountryRepository countryRepo;
    private final Long userId = 1L;


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class JoinUserToCountry {
        @Autowired
        private CountryUtil util;

        @BeforeAll
        public void beforeAll() {
            util.createCountry(countryName, "al");
        }

        @AfterAll
        public void afterAll() {
            util.deleteAllCountries();
        }

        @Test
        @Order(1)
        void userNotSubscribeBeforeJoin() {
            assertFalse(countryRepo.isUserSubscribed(countryName, userId));
        }

        @Test
        @Order(2)
        void userIsSubscribe_thenReturn200() throws Exception {
            MvcResult result = subscribeUser(countryName);
            assertEquals(200, result.getResponse().getStatus());
            assertTrue(countryRepo.isUserSubscribed(countryName, userId));
        }

        @Test
        @Order(3)
        void countryNotExists_thenThrowNotFound() {
            assertThrows(Exception.class, () -> subscribeUser("notExists"));
        }

    }

    private MvcResult subscribeUser(String countryName) throws Exception {
        return mockMvc.perform(post("/api/countries/" + countryName + "/join")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .with(user("admin").password("password"))
                        .content(userId.toString()))
                .andReturn();
    }
}*/