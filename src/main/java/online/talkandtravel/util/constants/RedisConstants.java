package online.talkandtravel.util.constants;

public class RedisConstants {
  public static final String USER_STATUS_KEY = "user:%s:isOnline";
  public static final String USER_LAST_SEEN_KEY = "user:%s:lastSeenOn";
  public static final String USER_REGISTER_DATA_REDIS_KEY_PATTERN = "register-user-data:%s";
  public static final String USER_REGISTER_DATA_REDIS_KEY_SEARCH_PATTERN = "register-user-data:*";
}
