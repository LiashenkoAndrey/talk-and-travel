package online.talkandtravel.controller.http;

import online.talkandtravel.model.dto.application.HealthStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class ApplicationHealthController {

  @GetMapping
  public HealthStatus healthStatus() {
    return new HealthStatus("OK");
  }
}
