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
  public static final String REGISTER_USER_PATH = API_BASE_PATH + "/authentication/register";
  public static final String LOGIN_USER_PATH = API_BASE_PATH + "/authentication/login";
  public static final String LOGOUT_USER_PATH = API_BASE_PATH + "/authentication/logout";
  public static final String CONFIRM_REGISTRATION_USER_PATH = API_BASE_PATH + "/authentication/registration-confirmation";
  public static final String GET_UNREAD_MESSAGES_PATH = API_BASE_PATH + "/chats/%s/messages/unread";
  public static final String CREATE_PRIVATE_CHAT_PATH = API_BASE_PATH + "/chats/private";
  public static final String FIND_MAIN_CHAT_PATH = API_V2_BASE_PATH + "/country/%s/main-chat";
  public static final String FIND_ALL_USER_PUBLIC_CHATS = API_V2_BASE_PATH + "/user/public-chats";

  // Application destinations prefixes
  public static final String APPLICATION_DESTINATION_PREFIX = "/request";
  public static final String CHAT_APPLICATION_DESTINATION = "/chat";
  public static final String AUTH_USER_APPLICATION_DESTINATION = "/auth-user";
  public static final String FULL_AUTH_USER_APPLICATION_DESTINATION = APPLICATION_DESTINATION_PREFIX + AUTH_USER_APPLICATION_DESTINATION;

  // Message broker prefixes
  public static final String BROKER_DESTINATION_PREFIX = "/notify";
  public static final String CHATS_BROKER_DESTINATION = BROKER_DESTINATION_PREFIX + "/chat";

  // Websocket endpoints
  public static final String HANDSHAKE_URI = "http://localhost:%s/ws";
  public static final String JOIN_CHAT_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.joinChat";
  public static final String START_TYPING_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.startTyping";
  public static final String STOP_TYPING_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.stopTyping";
  public static final String LEAVE_CHAT_EVENT_PATH = CHAT_APPLICATION_DESTINATION + "/events.leaveChat";
  public static final String SEND_MESSAGE_PATH = CHAT_APPLICATION_DESTINATION + "/messages";
  public static final String UPDATE_ONLINE_STATUS_EVENT_PATH = AUTH_USER_APPLICATION_DESTINATION + "/events.updateOnlineStatus";
  public static final String JOIN_CHAT_EVENT_FULL_PATH = APPLICATION_DESTINATION_PREFIX + JOIN_CHAT_EVENT_PATH;
  public static final String START_TYPING_EVENT_FULL_PATH = APPLICATION_DESTINATION_PREFIX + START_TYPING_EVENT_PATH;
  public static final String STOP_TYPING_EVENT_FULL_PATH = APPLICATION_DESTINATION_PREFIX + STOP_TYPING_EVENT_PATH;
  public static final String LEAVE_CHAT_EVENT_FULL_PATH = APPLICATION_DESTINATION_PREFIX + LEAVE_CHAT_EVENT_PATH;
  public static final String SEND_MESSAGE_FULL_PATH = APPLICATION_DESTINATION_PREFIX + SEND_MESSAGE_PATH;
  public static final String UPDATE_ONLINE_STATUS_FULL_PATH = FULL_AUTH_USER_APPLICATION_DESTINATION
      + "/events.updateOnlineStatus";

  // Websocket subscribe endpoints
  public static final String USER_WEBSOCKET_ERRORS_PATH = "/notify/user/%s/errors";
  public static final String USERS_ONLINE_STATUS_ENDPOINT = BROKER_DESTINATION_PREFIX + "/users/onlineStatus";
  public static final String MESSAGES_SUBSCRIBE_PATH = CHATS_BROKER_DESTINATION + "/%s/messages";
}
