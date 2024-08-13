package online.talkandtravel;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.util.dataManager.DataManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
    info =
        @Info(
            title = "Talk&Travel project",
            version = "0.0.1",
            description = "This is a chat application for people who like to travel"))
@RequiredArgsConstructor
@Log4j2
public class TalkAndTravelApplication {

  private final DataManager dataManager;

  public static void main(String[] args) {
    SpringApplication.run(TalkAndTravelApplication.class, args);
  }

  @Bean
  CommandLineRunner run() {
    return (args) -> {
      dataManager.checkAndPopulateCountries();
      dataManager.checkAndCreateChats();
      dataManager.addAdmin();
    };
  }
}
