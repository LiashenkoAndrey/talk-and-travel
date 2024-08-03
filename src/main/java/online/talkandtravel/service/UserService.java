package online.talkandtravel.service;

import online.talkandtravel.model.User;

import java.io.IOException;
import java.util.Optional;

public interface UserService {

    /**
     * Notifies all users which subscribed on /countries/{chatId}/texting-users that user started or
     * stopped typing
     * @param chatId chat id
     * @param userId user id
     * @param userName user name
     * @param userIsTexting a boolean indicating whether the user has started or stopped typing
     */
    void notifyAllThatUserStartOrStopTyping(Long chatId, Long userId, String userName, Boolean userIsTexting);

    User save(User user) throws IOException;

    User update(User user);

    Optional<User> findUserByEmail(String email);

    User findById(Long userId);

    boolean existsByEmail(String email);
}
