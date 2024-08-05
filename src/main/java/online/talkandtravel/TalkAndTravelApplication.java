package online.talkandtravel;

import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import java.io.IOException;
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
public class TalkAndTravelApplication {
    @Value("${USER_ADMIN_NAME}")
    private String adminName;
    @Value("${USER_ADMIN_EMAIL}")
    private String adminEmail;
    @Value("${USER_ADMIN_PASSWORD}")
    private String adminPassword;
    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(TalkAndTravelApplication.class, args);
    }

    @Bean
    CommandLineRunner run() {
        return args -> userService.findUserByEmail(adminEmail)
                .ifPresentOrElse(
                        user -> {},
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
