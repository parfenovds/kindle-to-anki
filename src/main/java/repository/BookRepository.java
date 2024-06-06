// Package repository - This package provides classes for dealing with the database

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

// Repository for retrieving BookInfo from the database
public enum BookRepository implements Repository<BookInfo> {
  INSTANCE;  // Singleton pattern instance

  private final BookDTOMapper bookDTOMapper = BookDTOMapper.INSTANCE;
  private static final String SELECT_BOOKINFO = """
      SELECT *  FROM book_info;
      """;

  public List<BookDTO> getAll() {
    List<BookInfo> bookInfoSet = new ArrayList<>();
    // Get all books from the database
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PathsHandler.getDatabaseAddress());
         PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOKINFO)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        bookInfoSet.add(getNextLookup(resultSet));
      }
    } catch (SQLException e) {
      ExceptionHandler.handleException(e);
    }
    // Map the list of BookInfo to a list of BookDTO
    return bookDTOMapper.mapAll(bookInfoSet);
  }

  private BookInfo getNextLookup(ResultSet resultSet) throws SQLException {
    // Map ResultSet to BookInfo
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
