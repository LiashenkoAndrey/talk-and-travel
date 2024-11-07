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

  private static final String TEMPLATE_FILE_NAME = "reset-password-email";
  private static final String APP_LOGO_FILE_PATH = "src/main/resources/templates/images/appLogo.jpg";
  private static final String APP_LOGO_CONTENT_ID = "logoImage";
  private static final String RESET_PASSWORD_CONTEXT_VARIABLE = "resetPasswordUrl";
  private static final String TOKEN_QUERY_PARAM_PATTERN = "?token=%s";
  private static final String FROM_PATTERN = "Talk_and_travel <%s>";
  private static final String PASSWORD_RECOVER_MESSAGE_SUBJECT = "Talk & travel. Password recover";

  @Value("${mail.username}")
  private String senderEmail;

  @Value("${RESET_PASSWORD_URL}")
  private String resetPasswordUrl;

  public void sendPasswordRecoverMessage(String to, String token) {
    try {
      log.info("Send email from: {}, to: {}", senderEmail, to);

      MimeMessage message = createMimeMessage(to, PASSWORD_RECOVER_MESSAGE_SUBJECT);
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setFrom(FROM_PATTERN.formatted(senderEmail));
      helper.setText(createPasswordRecoverHtmlContent(token), true);

      attachInlineImage(helper, APP_LOGO_CONTENT_ID, APP_LOGO_FILE_PATH);
      javaMailSender.send(message);

    } catch (Exception e) {
      log.error("An error occupied when send mail: {}", e.getMessage(), e);
    }
  }

  private String createPasswordRecoverHtmlContent(String token) {
    Context context = new Context();
    String urlWithToken = resetPasswordUrl + TOKEN_QUERY_PARAM_PATTERN.formatted(token);
    context.setVariable(RESET_PASSWORD_CONTEXT_VARIABLE, urlWithToken);

    return templateEngine.process(TEMPLATE_FILE_NAME, context);
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
