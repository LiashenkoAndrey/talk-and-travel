package online.talkandtravel.service;

import online.talkandtravel.model.User;

import java.io.IOException;
import java.util.Optional;

public interface UserService {

    User save(User user) throws IOException;

    User update(User user);

    Optional<User> findUserByEmail(String email);

    User findById(Long userId);

    boolean existsByEmail(String email);
}
