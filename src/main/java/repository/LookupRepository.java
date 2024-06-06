package repository;

import constant.DateOption;
import entity.Card;
import entity.Lookup;
import exception.ExceptionHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import util.Converter;
import util.PathsHandler;

// Repository for retrieving and filtering Lookup data from the database
public enum LookupRepository implements Repository<Card> {
  INSTANCE;  // Singleton pattern instance

  private static final String FILTERED_SELECT = """
      SELECT * FROM lookups
      WHERE timestamp BETWEEN ? AND ?
      AND word_key LIKE ?
      LIMIT ?;
      """;
  private static final String SELECT_MIN_TIMESTAMP = """
      SELECT MIN(timestamp) as timestamp FROM lookups;
      """;
  private static final String SELECT_MAX_TIMESTAMP = """
      SELECT MAX(timestamp) as timestamp FROM lookups;
      """;

  // Method to get filtered lookups from the database
  public Set<Lookup> getFiltered(Timestamp timestampFrom, Timestamp timestampTo, String sourceLanguage, Integer limit) {
    Set<Lookup> lookups = new HashSet<>();
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PathsHandler.getDatabaseAddress());
         PreparedStatement preparedStatement = connection.prepareStatement(FILTERED_SELECT)) {
      prepareStatementForFiltering(timestampFrom, timestampTo, sourceLanguage, preparedStatement, limit);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        lookups.add(getNextLookup(resultSet));
      }
    } catch (SQLException e) {
      ExceptionHandler.handleException(e);
    }
    return lookups;
  }

  // Method to set parameters for filtering query
  private static void prepareStatementForFiltering(
      Timestamp timestampFrom,
      Timestamp timestampTo,
      String sourceLanguage,
      PreparedStatement preparedStatement,
      Integer limit) throws SQLException {
    preparedStatement.setTimestamp(1, timestampFrom);
    preparedStatement.setTimestamp(2, timestampTo);
    preparedStatement.setString(3, sourceLanguage == null ? "%" : (sourceLanguage + ":%"));
    preparedStatement.setInt(4, limit);
  }

  // Method to map ResultSet to Lookup entity
  private static Lookup getNextLookup(ResultSet resultSet) throws SQLException {
    return Lookup.builder()
        .id(resultSet.getString("id"))
        .wordKey(resultSet.getString("word_key"))
        .bookKey(resultSet.getString("book_key"))
        .dictKey(resultSet.getString("dict_key"))
        .pos(resultSet.getString("pos"))
        .usage(resultSet.getString("usage"))
        .timestamp(resultSet.getTimestamp("timestamp"))
        .build();
  }

  // Method to get the minimum or maximum timestamp from the database
  public String getDateForLimit(DateOption dateOption) {
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PathsHandler.getDatabaseAddress());
         PreparedStatement preparedStatement =
             connection.prepareStatement(dateOption.equals(DateOption.MIN)
                 ? SELECT_MIN_TIMESTAMP
                 : SELECT_MAX_TIMESTAMP)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      return Converter.convertTimestampToString(resultSet.getTimestamp("timestamp"));
    } catch (SQLException e) {
      ExceptionHandler.handleException(e);
    }
    return null;
  }
}
