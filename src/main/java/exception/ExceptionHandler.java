package exception;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class ExceptionHandler {
  public static void handleException(Exception e) {
    log.error("An exception occurred: {}", e.getMessage());
    System.exit(1);
  }
}
