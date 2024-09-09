package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.util.UserUtils.USER_ID;
import static online.talkandtravel.util.service.EventDestination.USER_ONLINE_STATUS_DESTINATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.impl.UserEventServiceImpl;
import online.talkandtravel.util.service.EventPublisherUtil;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
@Log4j2
public class UserEventServiceImplTest {


  @Mock private EventPublisherUtil publisherUtil;

  @InjectMocks private UserEventServiceImpl underTest;

  @ParameterizedTest
  @MethodSource("updateUserOnlineStatusTestArgs")
  void updateUserOnlineStatusAndNotifyAllTest_shouldUpdateAndNotify(UserOnlineStatus isOnline, Long userId) {
    UserOnlineStatusDto dto = new UserOnlineStatusDto(userId, isOnline.isOnline());
    doNothing().when(publisherUtil).publishEvent(anyString(), any(UserOnlineStatusDto.class));

    underTest.publishUserOnlineStatusEvent(isOnline, userId );

    String expectedDestination = USER_ONLINE_STATUS_DESTINATION.formatted(userId);
    verify(publisherUtil, times(1)).publishEvent(expectedDestination, dto);
  }

  private static Stream<Arguments> updateUserOnlineStatusTestArgs() {
    return Stream.of(
        Arguments.of(UserOnlineStatus.ONLINE, USER_ID),
        Arguments.of(UserOnlineStatus.OFFLINE, USER_ID)
    );
  }

}
