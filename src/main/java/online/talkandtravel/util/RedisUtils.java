package online.talkandtravel.util;

import java.util.List;
import online.talkandtravel.exception.util.StringParseException;
import org.apache.commons.lang3.math.NumberUtils;

public class RedisUtils {

  public static final String USER_STATUS_KEY = "user:%s:isOnline";
  public static final String USER_STATUS_KEY_PATTERN = "user:*:isOnline";
  public static final String USER_LAST_SEEN_KEY = "user:%s:lastSeenOn";
  public static final String USER_LAST_SEEN_KEY_PATTERN = "user:*:lastSeenOn";

  /**
   * Extracts the user ID from the Redis key following the pattern:
   * "user:{userId}:isOnline".
   *
   * @param key the body of the Redis key message
   * @return the extracted user ID as a Long
   * @throws StringParseException if the user ID cannot be parsed as a long value
   */
  public static Long getUserIdFromRedisKey(String key) {
    String[] array = key.split(":");
    String userId = array[1]; //see pattern in java doc
    if (NumberUtils.isParsable(userId)) {
      return NumberUtils.toLong(userId);
    }
    throw new StringParseException(userId, "Can't parse a long value");
  }

  public static List<Long> getUserIdFromKeys(List<String> keys) {
    return keys.stream()
            .map(RedisUtils::getUserIdFromRedisKey)
            .toList();
  }

  public static String getUserStatusRedisKey(Long userId) {
    return String.format(USER_STATUS_KEY, userId);
  }

  public static List<String> getUserStatusRedisKeys(List<Long> usersIdList) {
    return usersIdList.stream()
            .map(RedisUtils::getUserStatusRedisKey)
            .toList();
  }
}