package online.talkandtravel.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

@Log4j2
public class CustomJSONConverter implements MessageConverter {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Object fromMessage(Message<?> message, Class<?> targetClass) {
    try {
      Object payload = message.getPayload();
      String jsonPayload = new String((byte[]) payload);
      return objectMapper.readValue(jsonPayload, targetClass);
    } catch (Exception e) {
      throw new MessageConversionException("Unsupported payload type");
    }
  }

  @Override
  public Message<?> toMessage(Object payload, MessageHeaders headers) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(payload);
      return new GenericMessage<>(jsonPayload.getBytes(), headers);
    } catch (Exception e) {
      throw new MessageConversionException("Failed to convert payload to message", e);
    }
  }
}
