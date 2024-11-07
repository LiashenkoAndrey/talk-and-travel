package online.talkandtravel.facade;

import online.talkandtravel.model.dto.auth.RecoverPasswordRequest;
import online.talkandtravel.model.dto.auth.UpdatePasswordRequest;

public interface RecoverPasswordFacade {

  void recoverPassword(RecoverPasswordRequest request);

  void updatePassword(UpdatePasswordRequest request);


}
