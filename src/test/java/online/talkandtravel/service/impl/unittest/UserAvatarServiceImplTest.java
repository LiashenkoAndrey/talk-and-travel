package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;
import online.talkandtravel.exception.avatar.UserAvatarNotFoundException;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.repository.AvatarRepository;
import online.talkandtravel.service.impl.UserAvatarServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserAvatarServiceImplTest {

  @Mock private AvatarRepository avatarRepository;

  @InjectMocks
  UserAvatarServiceImpl underTest;

  @Nested
  class FindById {
    private final Long userId = 1L;
    @Test
    void findByUserId_shouldThrow_whenNoAvatarFound() {
      when(avatarRepository.findByUserId(userId)).thenReturn(Optional.empty());
      assertThrows(UserAvatarNotFoundException.class, () -> underTest.findByUserId(userId));
    }

    @Test()
    void findByUserId_shouldThrow_whenInvalidID() {
      when(avatarRepository.findByUserId(userId)).thenThrow(RuntimeException.class);
      assertThrows(RuntimeException.class, () -> underTest.findByUserId(userId));
    }

    @ParameterizedTest
    @MethodSource("verifyExpectedResultArgs")
    void findByUserId_shouldResultDiffer_whenDifferentArguments(Long userId, Avatar expected) {
      verifyExpectedResult(userId, expected);
    }

    private static Stream<Arguments> verifyExpectedResultArgs() {
      return Stream.of(
          Arguments.of(1L, Avatar.builder().id(1L).build()),
          Arguments.of(2L, Avatar.builder().id(2L).build())
      );
    }

    private void verifyExpectedResult(Long userId, Avatar expected) {
      when(avatarRepository.findByUserId(userId)).thenReturn(Optional.ofNullable(expected));
      Avatar result = underTest.findByUserId(userId);
      assertEquals(expected, result);
      verify(avatarRepository, times(1)).findByUserId(userId);
    }
  }
}