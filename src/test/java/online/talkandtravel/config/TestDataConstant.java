package online.talkandtravel.config;

public final class TestDataConstant {

  private static final String SQL_PATH = "classpath:/testdata/";
  private static final String POSTFIX = ".sql";

  public static final String USER_COUNTRIES_DATA_SQL =
      SQL_PATH + "user-countries-test-data" + POSTFIX;
  public static final String USERS_DATA_SQL = SQL_PATH + "users-test-data" + POSTFIX;

  /**
   * Note: this file requires {@see USERS_DATA_SQL}
   */
  public static final String PRIVATE_CHATS_DATA_SQL =
      SQL_PATH + "private-chats-users-test-data" + POSTFIX;

  public static final String CHAT_MESSAGES_DATA_SQL =
      SQL_PATH + "chat-messages-test-data" + POSTFIX;

}
