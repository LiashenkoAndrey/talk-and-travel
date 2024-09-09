package online.talkandtravel.util;

import online.talkandtravel.model.entity.User;

public class UserUtils {

  public static final String
      USER_PASSWORD = "!123456Aa",
      USER_NAME = "Bob",
      USER_EMAIL = "bob@mail.com",
      USER_ABOUT = "about me";

  public static final Long USER_ID = 1L;

  public static User createDefaultUserWithId = User.builder().id(USER_ID).build();

  public static User createDefaultUser() {
    return User.builder()
        .id(USER_ID)
        .password(USER_PASSWORD)
        .userName(USER_NAME)
        .userEmail(USER_EMAIL)
        .build();
  }

}
