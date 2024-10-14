package online.talkandtravel.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
  @Override
  public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String dateTimeString = p.getText();
    // Truncate nanoseconds if there are more than 6 digits
    String truncated = dateTimeString.replaceFirst("(\\.\\d{6})\\d+", "$1");
    return ZonedDateTime.parse(truncated, DateTimeFormatter.ISO_ZONED_DATE_TIME);
  }
}
