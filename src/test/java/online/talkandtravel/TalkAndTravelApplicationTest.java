package online.talkandtravel;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.config.IntegrationTest;
import org.junit.jupiter.api.Test;

@Slf4j
class TalkAndTravelApplicationTest extends IntegrationTest {

  @Test
  void loadContext() {
    log.info("application context loads");
  }
}
