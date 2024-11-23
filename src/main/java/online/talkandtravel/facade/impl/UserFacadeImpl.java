package online.talkandtravel.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.UserFacade;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.UserService;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

  private final AuthenticationService authenticationService;
  private final UserService userService;
  private final AvatarService avatarService;

  @Override
  public void deleteUser() {
    User user = authenticationService.getAuthenticatedUser();
    log.info("Delete user with id: {}", user.getId());
    deleteAvatar(user);
    userService.deleteUser(user);
  }
  private void deleteAvatar(User user) {
    if (user.getAvatar() != null) {
      log.info("Delete user avatar with user id: {} and avatar key: {}", user.getId(), user.getAvatar().getKey().toString());
      avatarService.deleteByKey(user.getAvatar().getKey());
    } else {
      log.info("User with id: {} has no avatar", user.getId());
    }
  }
}
