package online.talkandtravel.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.RecoverPasswordFacade;
import online.talkandtravel.model.dto.auth.RecoverPasswordRequest;
import online.talkandtravel.model.dto.auth.UpdatePasswordRequest;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.MailService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class RecoverPasswordFacadeImpl implements RecoverPasswordFacade {

  private final UserService userService;
  private final TokenService tokenService;
  private final MailService mailService;

  @Override
  @Async
  public void recoverPassword(RecoverPasswordRequest request) {
    log.info("Recover user password, email: {}", request.userEmail());
    String email = request.userEmail();
    User user = userService.getUser(email);
    Token recoveryToken = tokenService.generatePasswordRecoveryToken(user);

    mailService.sendPasswordRecoverMessage(email, recoveryToken.getToken());
  }

  @Override
  public void updatePassword(UpdatePasswordRequest request) {
    log.info("Update user password");
    String tokenStr = request.token();
    Token token = tokenService.getToken(tokenStr);
    tokenService.validatePasswordRecoveryToken(token);
    userService.updateUserPassword(token.getUser(), request.newPassword());
    tokenService.deleteToken(token);
  }
}
