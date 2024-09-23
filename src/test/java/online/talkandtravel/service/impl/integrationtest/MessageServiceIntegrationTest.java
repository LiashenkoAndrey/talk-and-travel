package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.CHAT_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Principal;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.chat.UserNotJoinedTheChatException;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.MessageService;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Log4j2
@Sql({USERS_DATA_SQL, CHAT_DATA_SQL})
public class MessageServiceIntegrationTest extends IntegrationTest {

  @Autowired
  private TestAuthenticationService testAuthenticationService;

  @Autowired
  private MessageService underTest;

  private User bob, alise;

  private static final Long arubaChatId = 1L, existingMessageIdOfArubaChat = 1001L;

  @BeforeEach
  void init() {
    bob = getBob();
    alise = getAlice();
  }

  @Test
  void saveMessage_shouldThrow_whenUserNotJoinedInChat() {
    Principal principal = testAuthenticationService.authenticateUser(bob);

    SendMessageRequest request = new SendMessageRequest("content", arubaChatId, null);

    assertThrows(UserNotJoinedTheChatException.class, () -> underTest.saveMessage(request, principal));
  }

  @Test
  void saveMessage_shouldThrow_whenChatNotFound() {
    Principal principal = testAuthenticationService.authenticateUser(bob);
    Long notExistingChatId = 777L;
    SendMessageRequest request = new SendMessageRequest("content", notExistingChatId, null);

    assertThrows(UserNotJoinedTheChatException.class, () -> underTest.saveMessage(request, principal));
  }

  @Test
  void saveMessage_shouldThrow_whenRepliedMessageNotFound() {
    Principal principal = testAuthenticationService.authenticateUser(bob);
    Long repliedMessageId = 1000L;
    SendMessageRequest request = new SendMessageRequest("content", arubaChatId, repliedMessageId);

    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request, principal));
  }

  @Test
  void saveMessage_shouldThrow_whenRepliedMessageFromAnotherChat() {
    Principal principal = testAuthenticationService.authenticateUser(bob);
    Long angolaChatId = 3L;
    SendMessageRequest request = new SendMessageRequest("content", angolaChatId, existingMessageIdOfArubaChat);

    assertThrows(WebSocketException.class, () -> underTest.saveMessage(request, principal));
  }


  private static Stream<Arguments> saveMessageArgs() {
    return Stream.of(
        Arguments.of(getAlice(), 1L, null),
        Arguments.of(getAlice(), 2L, existingMessageIdOfArubaChat)
    );
  }

  @ParameterizedTest
  @MethodSource("saveMessageArgs")
  void saveMessage_shouldSave(User authUser, Long messageId, Long repliedMessageId) {
    Principal principal = testAuthenticationService.authenticateUser(authUser);
    String content = "content";
    SendMessageRequest request = new SendMessageRequest(content, arubaChatId, repliedMessageId);

    MessageDto actual = underTest.saveMessage(request, principal);

    assertEquals(messageId, actual.id());
    assertEquals(content, actual.content());
    assertEquals(MessageType.TEXT, actual.type());
    assertEquals(alise.getId(), actual.user().id());

    assertEquals(repliedMessageId, actual.repliedMessageId());
    assertNotNull(actual.creationDate());
  }
}
