package online.talkandtravel.util;

import static online.talkandtravel.util.constants.RedisConstants.USER_LAST_SEEN_KEY;
import static online.talkandtravel.util.constants.RedisConstants.USER_STATUS_KEY;

import java.util.List;
import online.talkandtravel.exception.util.StringParseException;
import org.apache.commons.lang3.math.NumberUtils;

public class RedisUtils {



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

  public static String getUserStatusRedisKey(Long userId) {
    return String.format(USER_STATUS_KEY, userId);
  }

  public static String getUserLastSeenKey(Long userId) {
    return String.format(USER_LAST_SEEN_KEY, userId);
  }

  public static List<String> getUserStatusRedisKeys(List<Long> usersIdList) {
    return usersIdList.stream()
            .map(RedisUtils::getUserStatusRedisKey)
            .toList();
  }

  public static List<String> getUserLastSeenRedisKeys(List<Long> usersIdList) {
    return usersIdList.stream()
        .map(RedisUtils::getUserLastSeenKey)
        .toList();
  }
}