package online.talkandtravel.util;

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

}
