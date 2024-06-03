package exception;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionHandler {
  public static void handleException(Exception e) {
    System.err.println("An exception occurred: " + e.getMessage());
    throw new RuntimeException(e);
  }
}
