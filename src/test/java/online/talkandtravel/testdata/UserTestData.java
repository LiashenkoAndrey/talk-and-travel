package online.talkandtravel.testdata;

import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;

public final class UserTestData {

  private static final Long ADMIN_ID = 1L;

  private UserTestData() {
    throw new UnsupportedOperationException(
        "Utility UserTestData class cannot be instantiated");
  }


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
