package util;

import constant.Constants;
import exception.ExceptionHandler;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathsHandler {
  private static final ThreadLocal<String> databaseAddressHolder = new ThreadLocal<>();
  private static final ThreadLocal<String> outputFileAddressHolder = new ThreadLocal<>();

  public static void setDatabaseAddress(String databaseAddress) {
    databaseAddressHolder.set(getFullValidDatabaseFilePath(databaseAddress));
  }

  public static String getDatabaseAddress() {
    return databaseAddressHolder.get();
  }

  public static void setOutputFileAddress(String outputFileAddress) {
    outputFileAddressHolder.set(getFullValidOutputFilePath(outputFileAddress));
  }

  public static String getOutputFileAddress() {
    return outputFileAddressHolder.get();
  }

  private static String getFullValidOutputFilePath(String outputFileAddress) {
    Path path = Paths.get(outputFileAddress);
    path = path.toAbsolutePath();
    return makePathEndingWithRegularFile(path, Constants.DEFAULT_OUTPUT_FILE_NAME).toString();
  }

  private static String getFullValidDatabaseFilePath(String probablePath) {
    Path path = Paths.get(probablePath);
    path = path.toAbsolutePath();
    path = makePathEndingWithRegularFile(path, Constants.DB_FILE_NAME);
    exceptionIfNoFile(path);
    return path.toString();
  }

  private static void exceptionIfNoFile(Path path) {
    try {
      if (!(Files.exists(path) && Files.isRegularFile(path))) {
        throw new FileNotFoundException("There's no vocab.db!");
      }
    } catch (FileNotFoundException e) {
      ExceptionHandler.handleException(e);
    }
  }

  private static Path makePathEndingWithRegularFile(Path path, String fileName) {
    if (!Files.isRegularFile(path)) {
      path = FileSystems.getDefault().getPath(path.toString(), fileName);
    }
    return path;
  }
}
