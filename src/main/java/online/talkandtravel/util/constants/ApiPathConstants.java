package online.talkandtravel.util.constants;


/**
 * This interface defines constants for API paths used throughout the application.
 *
 * <p>The constants in this interface provide a centralized way to manage and reference API paths,
 * ensuring consistency and ease of maintenance across the codebase. By defining the base path here,
 * any changes to the API structure only need to be updated in one place, reducing the risk of
 * errors and simplifying future modifications.
 *
 * <p>Constants:
 *
 * <ul>
 *   <li>{@link #API_BASE_PATH} - The base path for all API endpoints. It is used as the root path
 *       for constructing full API endpoints.
 * </ul>
 */
public final class ApiPathConstants {
  public static final String API_BASE_PATH = "/api";
  public static final String API_V2_BASE_PATH = "/api/v2";

  // HTTP endpoints
  public static final String GET_UNREAD_MESSAGES_PATH = API_BASE_PATH + "/chats/%s/messages/unread";
  public static final String CREATE_PRIVATE_CHAT_PATH = API_BASE_PATH + "/chats/private";
  public static final String FIND_MAIN_CHAT_PATH = API_V2_BASE_PATH + "/country/%s/main-chat";
  public static final String FIND_ALL_USER_PUBLIC_CHATS = API_V2_BASE_PATH + "/user/public-chats";

  // Application destinations prefixes
  public static final String APPLICATION_DESTINATION_PREFIX = "/request";
  public static final String CHAT_APPLICATION_DESTINATION = APPLICATION_DESTINATION_PREFIX + "/chat";
  public static final String AUTH_USER_APPLICATION_DESTINATION = APPLICATION_DESTINATION_PREFIX + "/auth-user";

  // Message brokers prefixes
  public static final String BROKER_DESTINATION_PREFIX = "/broadcast";
  public static final String CHATS_BROKER_DESTINATION = BROKER_DESTINATION_PREFIX + "/chat";
  public static final String USER_BROKER_DESTINATION = BROKER_DESTINATION_PREFIX + "/user";

  // Websocket endpoints
  public static final String HANDSHAKE_URI = "http://localhost:%s/ws";
  public static final String USER_WEBSOCKET_ERRORS_PATH = "/errors";
  public static final String JOIN_CHAT_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.joinChat";
  public static final String START_TYPING_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.startTyping";
  public static final String STOP_TYPING_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.stopTyping";
  public static final String LEAVE_CHAT_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.leaveChat";
  public static final String SEND_MESSAGE_PATH = CHAT_APPLICATION_DESTINATION + "/messages";
  public static final String UPDATE_ONLINE_STATUS_PATH = AUTH_USER_APPLICATION_DESTINATION + "/events.updateOnlineStatus";

  // Websocket subscribe endpoints
  public static final String USERS_ONLINE_STATUS_ENDPOINT = BROKER_DESTINATION_PREFIX + "/users/onlineStatus";
  public static final String MESSAGES_SUBSCRIBE_PATH = CHATS_BROKER_DESTINATION + "/%s/messages";
}
