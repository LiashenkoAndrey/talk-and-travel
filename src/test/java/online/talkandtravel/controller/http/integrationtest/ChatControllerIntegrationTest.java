package online.talkandtravel.controller.http.integrationtest;

import static online.talkandtravel.config.TestDataConstant.CHAT_MESSAGES_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.PRIVATE_CHATS_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.testdata.UserTestData.getTomas;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.util.TestAuthenticationService;
import online.talkandtravel.util.TestChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@Sql({USERS_DATA_SQL, PRIVATE_CHATS_DATA_SQL, CHAT_MESSAGES_DATA_SQL})
@Log4j2
class ChatControllerIntegrationTest extends IntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private TestAuthenticationService authenticationService;

  @Autowired private TestChatService testChatService;

  private static final String TOKEN_PREFIX = "Bearer ";

  private User alice, bob;

  @BeforeEach
  void init() {
    alice = getAlice();
    bob = getBob();
  }

  @Nested
  class CreatePrivateChat {

    private static final String CREATE_PRIVATE_CHAT_PATH = "/api/chats/private";

    @Test
    void createPrivateChat_shouldThrowConflict() throws Exception {
      performChatCreationByAlice(bob,
          status().isConflict());
    }

    @Test
    void createPrivateChat_shouldReturnChatId_whenRequestIsValid() throws Exception {
      performChatCreationByAlice(getTomas(),
          status().isOk(),
          jsonPath("$").isNumber());
    }

    @Test
    void createPrivateChat_shouldThrow_whenTheSameUser() throws Exception {
      performChatCreationByAlice(alice,
          status().isBadRequest());
    }

    private void performChatCreationByAlice(User companion, ResultMatcher... matcher)
        throws Exception {
      NewPrivateChatDto content = new NewPrivateChatDto(companion.getId()); //companion
      String aliceToken = authenticateUser(alice);

      mockMvc.perform(
              post(CREATE_PRIVATE_CHAT_PATH)
                  .headers(createAuthHeader(aliceToken))
                  .contentType(APPLICATION_JSON)
                  .content(toJson(content)))
          .andExpectAll(matcher);
    }

  }

  @Nested
  class FindMainChat {

    private static final String FIND_MAIN_CHAT_PATH = "/api/v2/country/%s/main-chat";

    private NewPrivateChatDto newPrivateChatDto;
    private String token;

    @BeforeEach
    void init() {
      newPrivateChatDto = new NewPrivateChatDto(alice.getId());
      token = authenticateUser(alice);
    }

    @ParameterizedTest
    @MethodSource("findMainChatArgs")
    void findMainChat_shouldFindChat(String name, String id) throws Exception {
      mockMvc.perform(
              get(FIND_MAIN_CHAT_PATH.formatted(name))
                  .headers(createAuthHeader(token))
                  .contentType(APPLICATION_JSON)
                  .content(toJson(newPrivateChatDto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("id").value(id))
          .andExpect(jsonPath("name").value(name));
    }

    @ParameterizedTest
    @MethodSource("findMainChat_shouldReturnNotFoundArgs")
    void findMainChat_shouldReturnNotFound(String countryName) throws Exception {
      mockMvc.perform(
              get(FIND_MAIN_CHAT_PATH.formatted(countryName))
                  .headers(createAuthHeader(token))
                  .contentType(APPLICATION_JSON)
                  .content(toJson(newPrivateChatDto)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("message").value("Country [%s] not found".formatted(countryName)));
    }

    private static Stream<Arguments> findMainChat_shouldReturnNotFoundArgs() {
      return Stream.of(
          Arguments.of("Incorrect"),
          Arguments.of(" ")
      );
    }

    private static Stream<Arguments> findMainChatArgs() {
      return Stream.of(
              Arguments.of("Aruba", "1"),
              Arguments.of("Angola", "3")
      );
    }
  }

  @Nested
  class UnreadMessages {

    private final NewPrivateChatDto newPrivateChatDto = new NewPrivateChatDto(getAlice().getId());
    private String token;
    private final String GET_UNREAD_MESSAGES_PATH = "/api/chats/%s/messages/unread";
    private final Long CHAT_ID = 10000L;

    @BeforeEach
    void init() {
      token = authenticateUser(getAlice());
    }

    @ParameterizedTest
    @MethodSource("getUnreadMessages_shouldReturnBadRequestArgs")
    void getUnreadMessages_shouldReturnBadRequest(String chatId, Integer status) throws Exception {
      mockMvc.perform(
              get(GET_UNREAD_MESSAGES_PATH.formatted(chatId))
                  .headers(createAuthHeader(token))
                  .contentType(APPLICATION_JSON)
                  .content(toJson(newPrivateChatDto)))
          .andExpect(status().is(status));
    }

    @Test
    void getUnreadMessages_shouldReturnEmptyPage() throws Exception {
      Long expectedLength = 0L;
      mockMvc.perform(
              get(GET_UNREAD_MESSAGES_PATH.formatted(CHAT_ID))
                  .headers(createAuthHeader(token))
                  .contentType(APPLICATION_JSON)
                  .content(toJson(newPrivateChatDto)))
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(expectedLength));
    }

    @Test
    void getUnreadMessages_shouldGetLastMessages() throws Exception {
      Long expectedLength = 4L;
      testChatService.setLastReadMessageId(CHAT_ID, alice.getId(), 6L);

      mockMvc.perform(
              get(GET_UNREAD_MESSAGES_PATH.formatted(CHAT_ID))
                  .headers(createAuthHeader(token))
                  .contentType(APPLICATION_JSON)
                  .content(toJson(newPrivateChatDto)))
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(expectedLength));
    }

    private static Stream<Arguments> getUnreadMessages_shouldReturnBadRequestArgs() {
      return Stream.of(
              Arguments.of("Incorrect", 400),
              Arguments.of(" ", 400),
              Arguments.of("-1", 400)
      );
    }

  }

  private HttpHeaders createAuthHeader(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", TOKEN_PREFIX + token);
    return headers;
  }

  private String authenticateUser(User user) {
    return authenticationService.loginAndGetToken(user.getUserEmail(), user.getPassword());
  }

  public String toJson(Object value) throws JsonProcessingException {
    return objectMapper.writeValueAsString(value);
  }
}
