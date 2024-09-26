package online.talkandtravel.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class StompMessageHandler<T> implements StompFrameHandler {
  private final Consumer<T> consumer;
  private final Class<T> targetType;

  public StompMessageHandler(Consumer<T> consumer, Class<T> targetType) {
    this.consumer = consumer;
    this.targetType = targetType;
  }

  @Override
  public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
    return Object.class;
  }
  @Override
  public void handleFrame(@NotNull StompHeaders headers, Object payload) {
    T message = parseJson(payload, targetType);
    consumer.accept(message);
  }

  private T parseJson(Object payload, Class<T> targetType) {
    try {
      String jsonString = new String((byte[]) payload, StandardCharsets.UTF_8);
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      return objectMapper.readValue(jsonString, targetType);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Json parse exception! Payload: %s, targetType: %s".formatted(payload, targetType),e);
    }
  }
}