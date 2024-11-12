package online.talkandtravel.service.impl;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Log4j2
public class MailServiceImpl implements MailService {

  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  private static final String RESET_PASSWORD_TEMPLATE_FILE_NAME = "reset-password-email";
  private static final String VERIFY_EMAIL_TEMPLATE_FILE_NAME = "verify-email";
  private static final String APP_LOGO_FILE_PATH = "src/main/resources/templates/images/appLogo.png";
  private static final String APP_LOGO_CONTENT_ID = "logoImage";
  private static final String URL_CONTEXT_VARIABLE = "url";
  private static final String TOKEN_QUERY_PARAM_PATTERN = "?token=%s";
  private static final String FROM_PATTERN = "Talk_and_travel <%s>";
  private static final String PASSWORD_RECOVER_MESSAGE_SUBJECT = "Talk & travel. Password recover";
  private static final String VERIFY_EMAIL_MESSAGE_SUBJECT = "Talk & travel. Confirm registration";

  @Value("${mail.username}")
  private String senderEmail;

  @Value("${RESET_PASSWORD_URL}")
  private String RESET_PASSWORD_URL;

  @Value("${CONFIRM_REGISTRATION_URL}")
  private String CONFIRM_REGISTRATION_URL;

  public void sendPasswordRecoverMessage(String to, String token) {
    log.info("Send recover password email from: {}, to: {}", senderEmail, to);
    verifyEmail(to, token, PASSWORD_RECOVER_MESSAGE_SUBJECT, RESET_PASSWORD_TEMPLATE_FILE_NAME, RESET_PASSWORD_URL);
  }

  @Override
  public void sendConfirmRegistrationMessage(String to, String token) {
    log.info("Send verify email from: {}, to: {}", senderEmail, to);
    verifyEmail(to, token, VERIFY_EMAIL_MESSAGE_SUBJECT, VERIFY_EMAIL_TEMPLATE_FILE_NAME, CONFIRM_REGISTRATION_URL);
  }

  private void verifyEmail(String to, String token, String subject, String template, String buttonUrl) {
    try {
      MimeMessage message = createMimeMessage(to, subject);
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setFrom(FROM_PATTERN.formatted(senderEmail));
      helper.setText(createConfirmButtonHtmlContent(template, token, buttonUrl), true);

      attachInlineImage(helper, APP_LOGO_CONTENT_ID, APP_LOGO_FILE_PATH);
      javaMailSender.send(message);

    } catch (Exception e) {
      log.error("An error occupied when send mail: {}", e.getMessage());
    }
  }

  private String createConfirmButtonHtmlContent(String template, String token, String url) {
    Context context = new Context();
    String urlWithToken = url + TOKEN_QUERY_PARAM_PATTERN.formatted(token);
    context.setVariable(URL_CONTEXT_VARIABLE, urlWithToken);

    return templateEngine.process(template, context);
  }

  private MimeMessage createMimeMessage(String to, String subject) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    message.setFrom(senderEmail);
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
    message.setSubject(subject);
    return message;
  }

  private void attachInlineImage(MimeMessageHelper helper, String contentId, String filePath)
      throws MessagingException {
    File imageFile = new File(filePath);
    if (imageFile.exists() && imageFile.isFile()) {
        helper.addInline(contentId, imageFile);
    } else {
      log.warn("Inline image not found at path: {}", filePath);
    }
  }
}
