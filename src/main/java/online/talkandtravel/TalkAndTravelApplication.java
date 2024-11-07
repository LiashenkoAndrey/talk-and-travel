package online.talkandtravel;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import java.util.Properties;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.MailService;
import online.talkandtravel.util.dataManager.DataManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication
@OpenAPIDefinition(
    info =
        @Info(
            title = "Talk&Travel project",
            version = "1.0.0",
            description = "This is a chat application for people who like to travel"))
@RequiredArgsConstructor
@Log4j2
public class TalkAndTravelApplication {

  private final DataManager dataManager;

  private final MailService mailService;


  public static void main(String[] args) {
    SpringApplication.run(TalkAndTravelApplication.class, args);
  }

  @Bean
  CommandLineRunner run() {
    return (args) -> {
      dataManager.checkAndPopulateCountries();
      dataManager.checkAndCreateChats();
      dataManager.prepareRedisData();
      dataManager.addAdmin();

      mailService.sendPasswordRecoverMessage("andrii.liashenko.pro@gmail.com", UUID.randomUUID().toString());
    };
  }
}
