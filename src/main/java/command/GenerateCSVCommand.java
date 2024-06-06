package command;

import lombok.extern.log4j.Log4j2;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.CardService;
import util.PathsHandler;

@Log4j2
@Command(name = "generate-csv", description = "Generates a CSV file from vocab.db", mixinStandardHelpOptions = true, footer = {
    "",
    "Examples:",
    "  java -jar kindle_to_anki-1.0-SNAPSHOT.jar generate-csv -t=ru -d=classes -o=classes -F=2015-09-07 -T=2015-09-07 -L=6 -l=http://45.142.36.134",
    "  java -jar kindle_to_anki-1.0-SNAPSHOT.jar generate-csv -t=ru -d=classes -l=http://45.142.36.134 -P=1716180208503"
})
public class GenerateCSVCommand implements Runnable {

  // Option for specifying the position to start exporting from (timestamp)
  @Option(names = {"-P", "--position"}, description = "Position to start (the number you've received after last CSV exporting, for example, " +
      "1441616116648). Flag -F ignores, if this flag used")
  private String timestamp;

  // Option for specifying the start date for filtering cards
  @Option(names = {"-F", "--date-from"}, description = "The date (yyyy-MM-dd, year-month-day) for the beginning of the period you want to receive " +
      "cards")
  private String dateFrom;

  // Option for specifying the end date for filtering cards
  @Option(names = {"-T", "--date-to"}, description = "The date (yyyy-MM-dd, year-month-day) for the end of the period you want to receive cards")
  private String dateTo;

  // Option for specifying the source language for filtering cards
  @Option(names = {"-s", "--source-language"}, description = "If set, only data in this language will be retrieved from the vocab.db")
  private String sourceLanguage;

  // Option for specifying the target language for translation
  @Option(required = true, names = {"-t", "--target-language"}, description = "Required. The language to which the translation will be produced")
  private String targetLanguage;

  // Option for specifying the address of the vocab.db file
  @Option(required = true, names = {"-d", "--database-address"}, description = "Required. Address of the vocab.db")
  private String databaseAddress;

  // Option for specifying the output file path for the CSV file
  @Option(names = {"-o", "--output-file"}, description = "Address for the output .csv file. Can be either a directory (in which case the file will " +
      "be named output.csv) or a file. If not set, the file output.csv will be saved in the directory where vocab.db is located")
  private String outputFilePath;

  // Option for specifying the address of the LibreTranslate server
  @Option(names = {"-l", "--libre-address"}, description = "Address to libretranslate including http or https, ex.: https://libretranslate.de; By " +
      "default http://localhost:5000 is set")
  private String libreAddress;

  // Option for specifying the maximum number of results to retrieve
  @Option(names = {"-L", "--limit"}, description = "Max amount of results")
  private Integer limit;

  private final CardService cardService = CardService.INSTANCE;

  @Override
  public void run() {
    log.info("starting");
    // Set database address, output file address, and LibreTranslate address
    PathsHandler.setDatabaseAddress(databaseAddress);
    PathsHandler.setOutputFileAddress(outputFilePath);
    PathsHandler.setLibreAddressHolder(libreAddress);
    // Process the cards with given parameters (date range, source language, target language, limit, timestamp)
    cardService.cardProceeding(
        dateFrom,
        dateTo,
        sourceLanguage,
        targetLanguage,
        limit,
        timestamp
    );
  }
}
