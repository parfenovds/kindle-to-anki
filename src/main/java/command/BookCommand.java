package command;

import dto.BookDTO;
import java.util.List;
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
    List<BookDTO> books = bookInfoService.getAll();
    int longestAuthor = books.stream().map(book -> book.getAuthor().length()).max(Integer::compareTo).get();
    System.out.println("Author" + " ".repeat(longestAuthor - 5) + "Title");
    books.forEach(book -> System.out.println(book.getAuthor() + " ".repeat(longestAuthor - book.getAuthor().length() + 1) + book.getTitle()));
  }
}
