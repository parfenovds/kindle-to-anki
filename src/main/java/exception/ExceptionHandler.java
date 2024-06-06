// Package exception - This package contains exception handling utilities

package exception;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

// Utility class for handling exceptions
@UtilityClass
@Log4j2
public class ExceptionHandler {
  public static void handleException(Exception e) {
    // Log the exception and exit the program with error code
    log.error("An exception occurred: {}", e.getMessage());
    System.exit(1);
  }
}
