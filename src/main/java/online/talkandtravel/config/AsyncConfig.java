package online.talkandtravel.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import online.talkandtravel.exception.model.AsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

  @Bean
  public Executor taskExecutor(
      @Value("${taskExecution.corePoolSize}") int corePoolSize,
      @Value("${taskExecution.maxPoolSize}") int maxPoolSize,
      @Value("${taskExecution.queueCapacity}") int queueCapacity

  ) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  /**
   * The {@link AsyncUncaughtExceptionHandler} instance to be used when an exception is thrown
   * during an asynchronous method execution with {@code void} return type.
   */
  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return new AsyncExceptionHandler();
  }
}
