package command;
// Package for command classes

import dto.BookDTO;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.BookInfoService;
import util.PathsHandler;

// Enables logging for this class
@Log4j2
// Defines the CLI command for retrieving book information
@Command(name = "books", description = "Getting all the books available", mixinStandardHelpOptions = true)
public class BookCommand implements Runnable {

  // Option for specifying the address of the vocab.db file
  @Option(required = true, names = {"-d", "--database-address"}, description = "Required. Address of the vocab.db")
  private String databaseAddress;
  private final BookInfoService bookInfoService = BookInfoService.INSTANCE;

  @Override
  public void run() {
    log.info("starting");
    // Set the database address
    PathsHandler.setDatabaseAddress(databaseAddress);
    // Get all books from the database
    List<BookDTO> books = bookInfoService.getAll();
    // Find the longest author name length
    int longestAuthor = books.stream().map(book -> book.getAuthor().length()).max(Integer::compareTo).get();
    // Print headers "Author" and "Title"
    System.out.println("Author" + " ".repeat(longestAuthor - 5) + "Title");
    // Print each book's author and title, aligned by the longest author name length
    books.forEach(book -> System.out.println(book.getAuthor() + " ".repeat(longestAuthor - book.getAuthor().length() + 1) + book.getTitle()));
  }
}