import controller.CardController;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(name = "KindleToAnki", version = "KindleToAnki 1.0", mixinStandardHelpOptions = true)
public class CliStarter implements Runnable {
  @Option(names = {"-F", "--date-from"}, description = "The date (yyyy-MM-dd, year-month-day, for the start of the day) for the beginning of the period you want to receive cards")
  private String dateFrom;
  @Option(names = {"-T", "--date-to"}, description = "The date (yyyy-MM-dd, year-month-day, for the end of the day) for the end of the period you want to receive cards")
  private String dateTo;
  @Option(names = {"-s", "--source-language"}, description = "If set, only words in this language will be received from the vocab.db")
  private String sourceLanguage;
  @Option(names = {"-b", "--book-title"}, description = "If set, only words from this book will be received from the vocab.db")
  private String bookTitle;
  @Option(required = true, names = {"-t", "--target-language"}, description = "Required. The language to which translation will be produced")
  private String targetLanguage;
  @Option(required = true, names = {"-d", "--database-address"}, description = "Required. Address for the actual vocab.db")
  private String databaseAddress;
  @Option(names = {"-o", "--output-file"}, description = "Address for the output .csv file")
  private String outputFilePath;
  private final static CardController cardController = CardController.INSTANCE;

  public static void main(String[] args) {
    int exitCode = new CommandLine(new CliStarter()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
//    cardController.makeCSV(
//        "2015-09-07",
//        "2015-09-07",
//        "en",
//        null,
//        "ru",
//        "/home/livcy/master/more/kindle_to_anki/target/classes/vocab.db",
//        "/home/livcy/master/more/kindle_to_anki/target/classes/output.csv"
//    );
    cardController.makeCSV(
        dateFrom,
        dateTo,
        sourceLanguage,
        bookTitle,
        targetLanguage,
        databaseAddress,
        outputFilePath
    );
  }
}
