package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.testdata.UserTestData.ALISE_LAST_SEEN_ON_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.ALISE_ONLINE_STATUS_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.BOB_LAST_SEEN_ON_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.BOB_ONLINE_STATUS_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.dto.user.OnlineStatusResponse;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.impl.OnlineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private User alise, bob;

    @BeforeEach
    void setUp() {
        underTest.KEY_EXPIRATION_DURATION_IN_SEC = 3L;
        valueOperations = Mockito.mock(ValueOperations.class);
        alise = getAlice();
        bob = getBob();
    }

    @Test
    void testUpdateUserOnlineStatus_SetOnline() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(eq(ALISE_ONLINE_STATUS_REDIS_KEY), eq("true"), any(Duration.class));

        OnlineStatusDto result = underTest.updateUserOnlineStatus(alise.getId(), true);

        verify(redisTemplate.opsForValue()).set(eq(ALISE_ONLINE_STATUS_REDIS_KEY), eq("true"), any(Duration.class));
        assertTrue(result.isOnline());
        assertEquals(alise.getId(), result.userId());

    }

    @Test
    void testUpdateUserOnlineStatus_SetOffline() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(eq(ALISE_LAST_SEEN_ON_REDIS_KEY), anyString());

        OnlineStatusDto result = underTest.updateUserOnlineStatus(alise.getId(), false);

        assertFalse(result.isOnline());
        assertEquals(alise.getId(), result.userId());

        verify(valueOperations).set(eq(ALISE_LAST_SEEN_ON_REDIS_KEY), anyString());
        verify(redisTemplate).delete(ALISE_ONLINE_STATUS_REDIS_KEY);
    }

    @Test
    void testGetAllUsersOnlineStatuses() {
        List<String> onlineStatusKey = List.of(ALISE_ONLINE_STATUS_REDIS_KEY);
        List<String> lastSeenKey = List.of(ALISE_LAST_SEEN_ON_REDIS_KEY);
        List<User> users = List.of(getAlice());
        ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(onlineStatusKey)).thenReturn(List.of("true"));
        when(valueOperations.multiGet(lastSeenKey)).thenReturn(List.of(time.toString()));
        when(userRepository.findAll()).thenReturn(users);

        Map<Long, OnlineStatusResponse> result = underTest.getAllUsersOnlineStatuses(List.of());

        OnlineStatusResponse response = result.get(alise.getId());
        assertNotNull(response);
        assertTrue(response.isOnline());
        assertEquals(time, response.lastSeenOn()) ;

        verify(redisTemplate, times(2)).opsForValue();
        verify(valueOperations).multiGet(onlineStatusKey);
        verify(valueOperations).multiGet(lastSeenKey);
        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsersOnlineStatusesForUsersList() {
        List<Long> userIds = List.of(alise.getId(), bob.getId());
        List<String> onlineStatusKeys = List.of(ALISE_ONLINE_STATUS_REDIS_KEY, BOB_ONLINE_STATUS_REDIS_KEY);
        List<String> lastSeenOnKeys = List.of(ALISE_LAST_SEEN_ON_REDIS_KEY, BOB_LAST_SEEN_ON_REDIS_KEY);
        ZonedDateTime aliseLastSeenOnTime =  ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime bobLastSeenOnTime =  ZonedDateTime.now(ZoneOffset.UTC).minusDays(12);
        boolean aliseOnlineStatus = true;
        boolean bobOnlineStatus = false;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(userRepository.findAllById(userIds)).thenReturn(List.of(alise, bob));
        when(valueOperations.multiGet(onlineStatusKeys)).thenReturn(List.of(Boolean.toString(aliseOnlineStatus),
            Boolean.toString(bobOnlineStatus)));
       when(valueOperations.multiGet(lastSeenOnKeys)).thenReturn(List.of(aliseLastSeenOnTime.toString(),
            bobLastSeenOnTime.toString()));

        Map<Long, OnlineStatusResponse> result = underTest.getAllUsersOnlineStatusesForUsersList(userIds);

        assertThat(result).hasSize(2);
        OnlineStatusResponse aliseOnlineStatusResponse = result.get(alise.getId());
        OnlineStatusResponse bobOnlineStatusResponse = result.get(bob.getId());

        assertTrue(aliseOnlineStatusResponse.isOnline());
        assertFalse(bobOnlineStatusResponse.isOnline());
        assertEquals(aliseLastSeenOnTime, aliseOnlineStatusResponse.lastSeenOn());
        assertEquals(bobLastSeenOnTime, bobOnlineStatusResponse.lastSeenOn());
    }
}
