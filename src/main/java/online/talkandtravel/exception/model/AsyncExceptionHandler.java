package online.talkandtravel.exception.model;

import java.lang.reflect.Method;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

  @SneakyThrows
  @Override
  public void handleUncaughtException(Throwable ex, Method method, Object... params) {
    Class<?> declaringClass = method.getDeclaringClass();
    log.error("Exception occupied during execution async method: {}.{}, Exception message: {}",
        declaringClass, method.getName(), ex.getMessage());
    throw ex;
  }
}
