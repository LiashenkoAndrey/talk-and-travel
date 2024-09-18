package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.util.RedisUtils.USER_STATUS_KEY_PATTERN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.impl.OnlineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
@Log4j2
public class OnlineServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private UserRepository userRepository;

    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OnlineServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest.KEY_EXPIRATION_DURATION_IN_MIN = 3L;
        valueOperations = Mockito.mock(ValueOperations.class);
    }

    @Test
    void testUpdateUserOnlineStatus_SetOnline() {
        Long userId = 1L;
        Boolean isOnline = true;
        String key = "user:1:isOnline";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        OnlineStatusDto result = underTest.updateUserOnlineStatus(userId, isOnline);

        verify(redisTemplate.opsForValue()).set(eq(key), eq("true"), any(Duration.class));
        assertTrue(result.isOnline());
        assertEquals(userId, result.userId());
    }

    @Test
    void testUpdateUserOnlineStatus_SetOffline() {
        Long userId = 1L;
        Boolean isOnline = false;
        String key = "user:1:isOnline";

        OnlineStatusDto result = underTest.updateUserOnlineStatus(userId, isOnline);

        verify(redisTemplate).delete(key);
        assertFalse(result.isOnline());
        assertEquals(userId, result.userId());
    }

    @Test
    void testGetUserOnlineStatusById_UserIsOnline() {
        Long userId = 1L;
        String key = "user:1:isOnline";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(redisTemplate.opsForValue().get(key)).thenReturn("true");
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Boolean result = underTest.getUserOnlineStatusById(userId);

        assertTrue(result);
        verify(redisTemplate.opsForValue()).get(key);
    }

    @Test
    void testGetUserOnlineStatusById_UserIsOffline() {
        Long userId = 1L;
        String key = "user:1:isOnline";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(key)).thenReturn("false");
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        Boolean result = underTest.getUserOnlineStatusById(userId);

        assertFalse(result);
        verify(redisTemplate.opsForValue()).get(key);
    }

    @Test
    void testGetUserOnlineStatusById_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            underTest.getUserOnlineStatusById(userId);
        });

        verify(userRepository).findById(userId);
    }

    @Test
    void testGetAllUsersOnlineStatuses() {
        Set<String> keys = Set.of("user:2:isOnline", "user:3:isOnline");
        List<User> users = List.of(getAlice(), getBob());

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.keys(USER_STATUS_KEY_PATTERN)).thenReturn(keys);

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        doAnswer(invocation -> {
            List<String> capturedKeys = invocation.getArgument(0);
            if (capturedKeys.get(0).equals("user:2:isOnline")) {
                return List.of("true", "false");
            } else {
                return List.of("false", "true");
            }
        }).when(valueOperations).multiGet(captor.capture());

        when(userRepository.findAll()).thenReturn(users);

        Map<Long, Boolean> result = underTest.getAllUsersOnlineStatuses(List.of());

        assertTrue(result.containsKey(2L));
        assertTrue(result.containsKey(3L));
        assertTrue(result.get(2L));
        assertFalse(result.get(3L));

    }

    @Test
    void testGetAllUsersOnlineStatusesForUsersList() {
        List<Long> userIds = List.of(2L, 3L);
        List<String> keys = List.of("user:2:isOnline", "user:3:isOnline");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(userRepository.findAllById(userIds)).thenReturn(List.of(getAlice(), getBob()));
        when(valueOperations.multiGet(keys)).thenReturn(List.of("true", "false"));

        Map<Long, Boolean> result = underTest.getAllUsersOnlineStatusesForUsersList(userIds);

        assertTrue(result.containsKey(2L));
        assertTrue(result.containsKey(3L));
        assertTrue(result.get(2L));
        assertFalse(result.get(3L));
    }
}
