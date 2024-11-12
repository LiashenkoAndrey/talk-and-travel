package online.talkandtravel.util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import online.talkandtravel.exception.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpUtils {

  public static ExceptionResponse createExceptionResponse(String message, HttpStatus httpStatus) {
    return new ExceptionResponse(message, httpStatus, ZonedDateTime.now(ZoneOffset.UTC), getRequestUri());
  }
  public static ExceptionResponse createExceptionResponse(String message, HttpStatus httpStatus, ZonedDateTime dateTime) {
    return new ExceptionResponse(message, httpStatus, dateTime, getRequestUri());
  }

  /**
   * Retrieves the URI of the current HTTP request.
   * <p>
   * This method uses {@link RequestContextHolder} to access the {@link ServletRequestAttributes}
   * of the current request, allowing it to be used in service, exception, or utility classes
   * without needing to pass {@link HttpServletRequest} directly.
   * If no request is present (e.g., in a non-HTTP context), the method returns {@code null}.
   * </p>
   *
   * @return the request URI as a {@link String}, or {@code null} if no request is available
   *         in the current context.
   */
  public static String getRequestUri() {
    Optional<ServletRequestAttributes> request = Optional.ofNullable(
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()));
    return request.map((requestAttributes) -> {
          String uri = requestAttributes.getRequest().getRequestURI();
          String queryString = Optional.ofNullable(requestAttributes.getRequest().getQueryString())
              .map((e) -> URLDecoder.decode(e, StandardCharsets.UTF_8))
              .orElse("");
          return queryString.isEmpty() ? uri : uri + "?" + queryString;
        })
        .orElse(null);
  }
}
