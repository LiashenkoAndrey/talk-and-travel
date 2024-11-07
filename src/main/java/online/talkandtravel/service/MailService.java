package online.talkandtravel.service;

public interface MailService {

  void sendPasswordRecoverMessage(String to, String token);

}
