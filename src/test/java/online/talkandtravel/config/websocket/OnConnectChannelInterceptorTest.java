package online.talkandtravel.config.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import online.talkandtravel.exception.token.InvalidTokenException;
import online.talkandtravel.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

class OnConnectChannelInterceptorTest {

  @Mock private TokenServiceImpl tokenService;

  @Mock private Message<?> message;

  @Mock MessageChannel messageChannel;
  @InjectMocks OnConnectChannelInterceptor underTest;

  @BeforeEach
  public void before() {
    MockitoAnnotations.openMocks(this);
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", "Bearer ");
    when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());
  }

  @Test
  void preSend_shouldReturnMessage_whenCommandNotConnect() {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
    accessor.setNativeHeader("Authorization", "Bearer ");
    Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    Message<?> result = underTest.preSend(message, messageChannel);

    assertEquals(message, result);
    verifyNoInteractions(tokenService);
  }

  @Test
  void preSend_shouldThrow_whenInvalidToken() {
    String token = "invalid token";
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", token);
    when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());

    MessageDeliveryException thrown =
        assertThrows(
            MessageDeliveryException.class,
            () -> underTest.preSend(message, messageChannel),
            "Authentication header Bearer is invalid");

    assertMessageEquals(thrown, "Authentication header '%s' is invalid", token);
  }

  @Test
  void preSend_shouldThrow_whenExpiredToken() {
    String token = "Bearer ", errorMessage = "InvalidTokenException";
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader("Authorization", token);
    when(message.getHeaders()).thenReturn(accessor.getMessageHeaders());
    doThrow(new InvalidTokenException(errorMessage, errorMessage))
        .when(tokenService)
        .validateTokenAndGetUserId(anyString());

    MessageDeliveryException thrown =
        assertThrows(
            MessageDeliveryException.class, () -> underTest.preSend(message, messageChannel), "Af");

    assertMessageEquals(thrown, errorMessage);
  }

  private void assertMessageEquals(
      MessageDeliveryException actual, String expected, String... params) {
    assertEquals(String.format(expected, (Object[]) params), actual.getMessage());
  }
}
