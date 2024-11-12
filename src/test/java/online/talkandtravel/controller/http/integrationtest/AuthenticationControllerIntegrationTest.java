package online.talkandtravel.controller.http.integrationtest;

import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.util.constants.RedisConstants.USER_REGISTER_DATA_REDIS_KEY_SEARCH_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.MailService;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

@Log4j2
public class AuthenticationControllerIntegrationTest extends IntegrationTest {

  @Autowired private TestAuthenticationService testAuthenticationService;

  @Autowired private RedisTemplate<String, RegisterRequest> redisTemplate;

  @MockBean private MailService mockMailService;

  private User alice;

  @BeforeEach
  void init() {
    alice = getAlice();
  }

  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class RegisterTest {

    @Test
    @Order(1)
    void shouldSaveUserDataToTempStorage_whenRegisterDataValid() throws Exception {
      RegisterRequest request = new RegisterRequest(alice.getUserName(), alice.getUserEmail(), alice.getPassword());

      assertThat(getKeys()).hasSize(0);

      testAuthenticationService.register(request).andExpect(status().isAccepted());
      Thread.sleep(200);

      assertThat(getKeys()).hasSize(1);
      verify(mockMailService).sendConfirmRegistrationMessage(eq(alice.getUserEmail()), anyString());
    }

    @Test
    @Order(2)
    void shouldSaveUser_whenConfirmRegistration() throws Exception {
      assertThat(getKeys()).hasSize(1);
      String key = getKeys().iterator().next();
      String token = key.split(":")[1];

      testAuthenticationService.confirmUserRegistration(token)
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.token").isNotEmpty())
          .andExpect(jsonPath("$.userDto").exists())
          .andExpect(jsonPath("$.userDto.id").exists())
          .andExpect(jsonPath("$.userDto.userName").value(alice.getUserName()))
          .andExpect(jsonPath("$.userDto.userEmail").value(alice.getUserEmail()))
          .andExpect(jsonPath("$.userDto.about").isEmpty())
          .andExpect(jsonPath("$.userDto.avatar").isEmpty());
    }

    private Set<String> getKeys() {
      Set<String> allKeys = redisTemplate.keys(USER_REGISTER_DATA_REDIS_KEY_SEARCH_PATTERN);
      log.info("keys: {}", allKeys);
      return allKeys;
    }
  }

}
