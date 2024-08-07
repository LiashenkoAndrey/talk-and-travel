package online.talkandtravel;

import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.util.createCountryChats.CountryChatManager;
import online.talkandtravel.util.fillCountryTable.CountryTableManager;
import online.talkandtravel.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "online.talkandtravel")
@OpenAPIDefinition(
    info = @Info(
        title = "Talk&Travel project",
        version = "0.0.1",
        description = "This is a chat application for people who like to travel"
    )
)
@RequiredArgsConstructor
@Log4j2
public class TalkAndTravelApplication {

  @Value("${USER_ADMIN_NAME}")
  private String adminName;
  @Value("${USER_ADMIN_EMAIL}")
  private String adminEmail;
  @Value("${USER_ADMIN_PASSWORD}")
  private String adminPassword;
  private final UserService userService;

  private final CountryTableManager countryTableManager;

  private final CountryChatManager countryChatManager;

  public static void main(String[] args) {
    SpringApplication.run(TalkAndTravelApplication.class, args);
  }


  @Bean
  CommandLineRunner run() {
    return (args) -> {
      countryTableManager.readJsonAndSaveAllIfTableIsEmpty();
      countryChatManager.checkAndCreateChats();
      addAdmin();

    };
  }


  private void addAdmin() {
    userService.findUserByEmail(adminEmail)
        .ifPresentOrElse(
            user -> {
            },
            () -> {
              try {
                userService.save(User.builder()
                    .userName(adminName)
                    .userEmail(adminEmail)
                    .password(adminPassword)
                    .role(Role.ADMIN)
                    .build());
              } catch (IOException e) {
                throw new RuntimeException("Cant generate standard avatar.");
              }
            }
        );
  }
}
