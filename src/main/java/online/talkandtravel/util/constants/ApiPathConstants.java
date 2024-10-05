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
public interface ApiPathConstants {
  String API_BASE_PATH = "/api";
  String API_V2_BASE_PATH = "/api/v2";
  String USERS_ONLINE_STATUS_ENDPOINT = "/users/onlineStatus";
  String GET_UNREAD_MESSAGES_PATH = API_BASE_PATH + "/chats/%s/messages/unread";
  String CREATE_PRIVATE_CHAT_PATH = API_BASE_PATH + "/chats/private";
  String FIND_MAIN_CHAT_PATH = API_V2_BASE_PATH + "/country/%s/main-chat";
  String FIND_ALL_USER_PUBLIC_CHATS = API_V2_BASE_PATH + "/user/public-chats";
}
