package online.talkandtravel.config;

import online.talkandtravel.TalkAndTravelApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for all Integration test classes. All integration test classes should extend this
 * class.
 */
@Transactional
@Import({TestContainerConfig.class})
@SpringBootTest(classes = TalkAndTravelApplication.class)
public abstract class IntegrationTest {}
