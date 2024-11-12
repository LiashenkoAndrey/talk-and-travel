package online.talkandtravel.testdata;

import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;

public final class UserTestData {
  public static final String ALISE_ONLINE_STATUS_REDIS_KEY = "user:2:isOnline";
  public static final String ALISE_LAST_SEEN_ON_REDIS_KEY = "user:2:lastSeenOn";
  public static final String BOB_ONLINE_STATUS_REDIS_KEY = "user:3:isOnline";
  public static final String BOB_LAST_SEEN_ON_REDIS_KEY = "user:3:lastSeenOn";

  public static final String TOMAS_ONLINE_STATUS_REDIS_KEY = "user:4:isOnline";
  public static final String TOMAS_LAST_SEEN_ON_REDIS_KEY = "user:4:lastSeenOn";

  public static final String ONLINE_STATUS_REDIS_KEY_PATTERN = "user:%s:isOnline";
  public static final String LAST_SEEN_ON_REDIS_KEY_PATTERN = "user:%s:lastSeenOn";

  public static Long ALICE_ID = 2L, BOB_ID = 3L, TOMAS_ID = 4L;


  public static User getAlice(){
    return User.builder()
        .id(2L)
        .userName("Alice")
        .password("!123456Aa")
        .userEmail("alice@mail.com")
        .about("Hello, I am Alice!")
        .role(Role.USER)
        .build();
  }

  public static UserDtoBasic getAliceDtoBasic() {
    User alice = getAlice();
    return new UserDtoBasic(alice.getId(), alice.getUserName(), alice.getUserEmail(),
        alice.getAbout(), new AvatarDto());
  }

  public static User getAliceSaved(){
    return User.builder()
        .id(2L)
        .userName("Alice")
        .password("$2a$10$QDyNQbb6B6EyEb4ZLJ6TR.ogaD4mvmwr6BTszgSUCisONGUUYp4KG")
        .userEmail("alice@mail.com")
        .about("Hello, I am Alice!")
        .role(Role.USER)
        .build();
  }

  public static User getBob(){
    return User.builder()
        .id(3L)
        .userName("Bob")
        .password("!123456Bb")
        .userEmail("bob@mail.com")
        .about("Hello, I am Bob!")
        .role(Role.USER)
        .build();
  }

  public static User getBobSaved(){
    return User.builder()
        .id(3L)
        .userName("Bob")
        .password("$2a$12$BQ33GZDbIiudkjlE4yHDZe6uJBiBtTK4vLNoCWJoE2agA9TS9U7uO")
        .userEmail("bob@mail.com")
        .about("Hello, I am Bob!")
        .role(Role.USER)
        .build();
  }

  /**
   * Tomas doesn't have private chats
   */
  public static User getTomas(){
    return User.builder()
        .id(4L)
        .userName("Tomas")
        .password("!123456Bb")
        .userEmail("tomas@gmail.com")
        .about("I am Tomas!")
        .role(Role.USER)
        .build();
  }
}
