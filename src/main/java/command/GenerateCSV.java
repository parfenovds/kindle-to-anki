package command;

import lombok.extern.log4j.Log4j2;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.CardService;
import util.PathsHandler;

@Log4j2
@Command(name = "generate-csv", description = "Generates a CSV file from vocab.db")
public class GenerateCSV implements Runnable {
  @Option(names = {"-F", "--date-from"}, description = "The date (yyyy-MM-dd, year-month-day) for the beginning of the period you want to receive cards")
  private String dateFrom;

  @Option(names = {"-T", "--date-to"}, description = "The date (yyyy-MM-dd, year-month-day) for the end of the period you want to receive cards")
  private String dateTo;

  @Option(names = {"-s", "--source-language"}, description = "If set, only data in this language will be retrieved from the vocab.db")
  private String sourceLanguage;

  @Option(names = {"-b", "--book-title"}, description = "If set, only data from this book will be retrieved from the vocab.db")
  private String bookTitle;

  @Option(required = true, names = {"-t", "--target-language"}, description = "Required. The language to which the translation will be produced")
  private String targetLanguage;

  @Option(required = true, names = {"-d", "--database-address"}, description = "Required. Address of the vocab.db")
  private String databaseAddress;

  @Option(names = {"-o", "--output-file"}, description = "Address for the output .csv file. Can be either a directory (in which case the file will be named output.csv) or a file. If not set, the file output.csv will be saved in the directory where vocab.db is located")
  private String outputFilePath;

  @Option(names = {"-l", "--libre-address"}, description = "Address to libretranslate including http or https, ex.: https://libretranslate.de")
  private String libreAddress;

  private final CardService cardService = CardService.INSTANCE;

  @Override
  public void run() {
    log.info("starting");
    PathsHandler.setDatabaseAddress(databaseAddress);
    PathsHandler.setOutputFileAddress(outputFilePath);
    PathsHandler.setLibreAddressHolder(libreAddress);
    cardService.cardProceeding(
        dateFrom,
        dateTo,
        sourceLanguage,
        bookTitle,
        targetLanguage
    );
  }
}
