package online.talkandtravel.util.service;

public class EventDestination {
  public static final String CHAT_MESSAGE_DESTINATION = "/countries/%s/messages";
  public static final String USER_ONLINE_STATUS_DESTINATION = "/userOnlineStatus/%s";
  public static final String USER_STATUS_KEY = "user:%s:isOnline";

  public static String getUserStatusKey(Long userId) {
    return String.format(USER_STATUS_KEY, userId);
  }
}