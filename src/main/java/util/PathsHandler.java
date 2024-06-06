package util;

// Utility class for handling file paths
import constant.Constants;
import exception.ExceptionHandler;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class PathsHandler {
  private static final ThreadLocal<String> databaseAddressHolder = new ThreadLocal<>();
  private static final ThreadLocal<String> outputFileAddressHolder = new ThreadLocal<>();
  private static final ThreadLocal<String> libreAddressHolder = new ThreadLocal<>();

  public static void setDatabaseAddress(String databaseAddress) {
    // Set the database address
    databaseAddressHolder.set(getFullValidDatabaseFilePath(databaseAddress));
    log.info("DB address is set to {}", databaseAddressHolder.get());
  }

  public static String getDatabaseAddress() {
    // Get the database address
    return databaseAddressHolder.get();
  }

  public static void setOutputFileAddress(String outputFileAddress) {
    // Set the output file address
    outputFileAddressHolder.set(getFullValidOutputFilePath(outputFileAddress));
    log.info("Output address is set to {}", outputFileAddressHolder.get());
  }

  public static String getLibreAddressHolder() {
    // Get the LibreTranslate address
    return libreAddressHolder.get();
  }

  public static void setLibreAddressHolder(String libreAddress) {
    // Set the LibreTranslate address
    libreAddressHolder.set(libreAddress == null ? Constants.LIBRETRANSLATE_URL : libreAddress);
  }

  public static String getOutputFileAddress() {
    // Get the output file address
    return outputFileAddressHolder.get();
  }

  private static String getFullValidOutputFilePath(String outputFileAddress) {
    // Get the full valid output file path
    if (outputFileAddress == null) {
      outputFileAddress = databaseAddressHolder.get();
      Path path1 = Paths.get(outputFileAddress);
      if (Files.isRegularFile(path1)) {
        path1 = path1.getParent();
      }
      outputFileAddress = path1.toString();
    }
    Path path = Paths.get(outputFileAddress);
    path = path.toAbsolutePath();
    return makePathEndingWithRegularFile(path, Constants.DEFAULT_OUTPUT_FILE_NAME).toString();
  }

  private static String getFullValidDatabaseFilePath(String probablePath) {
    // Get the full valid database file path
    Path path = Paths.get(probablePath);
    path = path.toAbsolutePath();
    path = makePathEndingWithRegularFile(path, Constants.DB_FILE_NAME);
    exceptionIfNoFile(path);
    return path.toString();
  }

  private static void exceptionIfNoFile(Path path) {
    // Throw exception if the file does not exist
    try {
      if (!(Files.exists(path) && Files.isRegularFile(path))) {
        throw new FileNotFoundException("There's no vocab.db!");
      }
    } catch (FileNotFoundException e) {
      ExceptionHandler.handleException(e);
    }
  }

  private static Path makePathEndingWithRegularFile(Path path, String fileName) {
    // Ensure the path ends with a regular file
    if (Files.isDirectory(path)) {
      path = FileSystems.getDefault().getPath(path.toString(), fileName);
    }
    return path;
  }
}
