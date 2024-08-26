package online.talkandtravel.controller.http;

import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@Sql(USERS_DATA_SQL)
class ChatControllerIntegrationTest extends IntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private TestAuthenticationService authenticationService;

  @Test
  void createPrivateChat_shouldReturnChatId_whenRequestIsValid() throws Exception {
    User alice = getAlice();
    User bob = getBob();

    NewPrivateChatDto newPrivateChatDto = new NewPrivateChatDto(alice.getId(), bob.getId());

    String token =
        authenticationService.loginAndGetToken(alice.getUserEmail(), alice.getPassword());

    // Act & Assert
    mockMvc
        .perform(
            post("/api/chats/private")
                .header(
                    "Authorization",
                    "Bearer " + token) // Add Authorization header with Bearer token
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPrivateChatDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNumber());
  }
}
