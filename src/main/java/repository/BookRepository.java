package repository;

import dto.BookDTO;
import entity.BookInfo;
import exception.ExceptionHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mapper.BookDTOMapper;
import util.PathsHandler;

public enum BookRepository implements Repository<BookInfo> {
  INSTANCE;
  private final BookDTOMapper bookDTOMapper = BookDTOMapper.INSTANCE;
  private static final String SELECT_BOOKINFO = """
      SELECT *  FROM book_info;
      """;
  public List<BookDTO> getAll() {
    List<BookInfo> bookInfoSet = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PathsHandler.getDatabaseAddress());
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOKINFO)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        bookInfoSet.add(getNextLookup(resultSet));
      }
    } catch (SQLException e) {
      ExceptionHandler.handleException(e);
    }
    return bookDTOMapper.mapAll(bookInfoSet);
  }

  private BookInfo getNextLookup(ResultSet resultSet) throws SQLException {
    return BookInfo.builder()
        .id(resultSet.getString("id"))
        .asin(resultSet.getString("asin"))
        .guid(resultSet.getString("guid"))
        .lang(resultSet.getString("lang"))
        .title(resultSet.getString("title"))
        .authors(resultSet.getString("authors"))
        .build();
  }
}
