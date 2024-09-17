package online.talkandtravel.service.impl.unittest;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Log4j2
public class OnlineServiceImplTest {

    @Mock RedisTemplate<String, String> redisTemplate;

    @Mock UserRepository userRepository;

    @InjectMocks OnlineServiceImpl underTest;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void updateUserOnlineStatus() {
        Principal principal = createPrincipalFromUser(user);

        Boolean isOnline = true;

        doNothingWhenRedisTemplate();
        OnlineStatusDto result = underTest.updateUserOnlineStatus(principal, isOnline);

        assertEquals(isOnline, result.isOnline());
        assertEquals(user.getId(), result.userId());
    }

    private void doNothingWhenRedisTemplate() {
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(valueOperations).set(anyString(), anyString(), any());
        doNothing().when(valueOperations).set(anyString(), anyString());
        doNothing().when(valueOperations).set(anyString(), anyString(), any(), any());
        doNothing().when(valueOperations).get(anyString());

    }

    private Principal createPrincipalFromUser(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
    }
}
