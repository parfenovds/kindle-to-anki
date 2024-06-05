package command;

import lombok.extern.log4j.Log4j2;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.BookInfoService;
import util.PathsHandler;

@Log4j2
@Command(name = "books", description = "Getting all the books available")
public class BookCommand implements Runnable {

  @Option(required = true, names = {"-d", "--database-address"}, description = "Required. Address of the vocab.db")
  private String databaseAddress;
  private final BookInfoService bookInfoService = BookInfoService.INSTANCE;
  @Override
  public void run() {
    log.info("starting");
    PathsHandler.setDatabaseAddress(databaseAddress);
    bookInfoService.getAll().forEach(System.out::println);
  }
}
