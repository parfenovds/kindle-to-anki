package util;

import exception.ExceptionHandler;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Converter {
  public static Timestamp convertStringToTimestamp(String dateString, Boolean startOfDay) {
    LocalDateTime dateTime;
    try {
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate parsedDate = LocalDate.parse(dateString, dateFormatter);
      if (startOfDay) {
        dateTime = parsedDate.atStartOfDay();
      } else {
        dateTime = parsedDate.atTime(23, 59, 59, 999_999_999);
      }
    } catch (DateTimeParseException e) {
      ExceptionHandler.handleException(e);
      return null;
    }
    return Timestamp.valueOf(dateTime);
  }
  public static String convertTimestampToString(Timestamp timestamp) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.format(timestamp);
  }
}
