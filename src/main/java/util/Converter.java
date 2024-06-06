package util;

// Utility class for converting dates and timestamps
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
    // Convert date string to Timestamp
    LocalDateTime dateTime;
    try {
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate parsedDate = LocalDate.parse(dateString, dateFormatter);
      if (startOfDay) {
        // Set time to start of day if startOfDay is true
        dateTime = parsedDate.atStartOfDay();
      } else {
        // Set time to end of day if startOfDay is false
        dateTime = parsedDate.atTime(23, 59, 59, 999_999_999);
      }
    } catch (DateTimeParseException e) {
      // Handle exception if date string cannot be parsed
      ExceptionHandler.handleException(e);
      return null;
    }
    return Timestamp.valueOf(dateTime);
  }

  public static String convertTimestampToString(Timestamp timestamp) {
    // Convert Timestamp to date string
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.format(timestamp);
  }
}
