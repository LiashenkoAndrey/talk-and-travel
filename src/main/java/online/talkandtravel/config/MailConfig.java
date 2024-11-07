package online.talkandtravel.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

  @Bean
  public JavaMailSender getJavaMailSender(
      @Value("${mail.username}") String senderEmail,
      @Value("${mail.password}") String senderPassword,
      @Value("${mail.host}") String host,
      @Value("${mail.port}") int port,
      @Value("${mail.transport.protocol}") String protocol,
      @Value("${mail.smtp.auth}") String smtpAuth,
      @Value("${mail.smtp.starttls.enable}") String isStarttlsEnable,
      @Value("${mail.debug}") String isDebugEnabled

  ) {
    System.out.println(isStarttlsEnable);

    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);

    mailSender.setPassword(senderPassword);
    mailSender.setUsername(senderEmail);

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", protocol);
    props.put("mail.smtp.auth", smtpAuth);
    props.put("mail.smtp.starttls.enable", isStarttlsEnable);
    props.put("mail.debug", isDebugEnabled);

    return mailSender;
  }
}
