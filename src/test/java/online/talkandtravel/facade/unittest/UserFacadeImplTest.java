package online.talkandtravel.facade.unittest;

import static online.talkandtravel.util.UserUtils.createDefaultUser;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.impl.UserFacadeImpl;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.UserService;
import online.talkandtravel.service.event.UserEventService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Log4j2
class UserFacadeImplTest {

  @Mock private UserEventService userEventService;

  @Mock private UserService userService;

  @InjectMocks
  private UserFacadeImpl underTest;

  private static final Long USER_ID = 1L;
  private final User user = createDefaultUser();

  @ParameterizedTest
  @MethodSource("updateUserOnlineStatusTestArgs")
  void updateUserOnlineStatusAndNotifyAllTest_shouldUpdateAndNotify(UserOnlineStatus userOnlineStatus) {
    updateUserOnlineStatusAndNotifyAllTest(userOnlineStatus);
  }

  private static Stream<Arguments> updateUserOnlineStatusTestArgs() {
    return Stream.of(
        Arguments.of(UserOnlineStatus.ONLINE),
        Arguments.of(UserOnlineStatus.OFFLINE)
    );
  }

  private void updateUserOnlineStatusAndNotifyAllTest(UserOnlineStatus status) {
    doNothing().when(userService).updateUserOnlineStatus(status, user);
    doNothing().when(userEventService).publishUserOnlineStatusEvent(status, USER_ID);

    underTest.updateUserOnlineStatusAndNotifyAll(status);

    verify(userService, times(1)).updateUserOnlineStatus(status, user);
    verify(userEventService, times(1)).publishUserOnlineStatusEvent(status, USER_ID);
  }
}