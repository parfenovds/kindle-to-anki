package repository;

import constant.DateOption;
import entity.Card;
import entity.Lookup;
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

public enum LookupRepository implements Repository<Card> {
  INSTANCE;
  private static final String FILTERED_SELECT = """
      SELECT * FROM lookups
      WHERE timestamp BETWEEN ? AND ?
      AND word_key LIKE ?
      AND book_key LIKE ?;
      """;
  private static final String SELECT_MIN_TIMESTAMP = """
      SELECT MIN(timestamp) as timestamp FROM lookups;
      """;
  private static final String SELECT_MAX_TIMESTAMP = """
      SELECT MAX(timestamp) as timestamp FROM lookups;
      """;

  public Set<Lookup> getFiltered(Timestamp timestampFrom, Timestamp timestampTo, String sourceLanguage, String bookTitle) {
    Set<Lookup> lookups = new HashSet<>();
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PathsHandler.getDatabaseAddress());
         PreparedStatement preparedStatement = connection.prepareStatement(FILTERED_SELECT)) {
      prepareStatementForFiltering(timestampFrom, timestampTo, sourceLanguage, bookTitle, preparedStatement);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        lookups.add(getNextLookup(resultSet));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return lookups;
  }

  private static void prepareStatementForFiltering(
      Timestamp timestampFrom,
      Timestamp timestampTo,
      String sourceLanguage,
      String bookTitle,
      PreparedStatement preparedStatement) throws SQLException {
    preparedStatement.setTimestamp(1, timestampFrom);
    preparedStatement.setTimestamp(2, timestampTo);
    preparedStatement.setString(3, sourceLanguage == null ? "%" : (sourceLanguage + ":%"));
    preparedStatement.setString(4, bookTitle == null ? "%" : bookTitle);
  }

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

  public String getDateForLimit(DateOption dateOption) {
    String result;
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + PathsHandler.getDatabaseAddress());
         PreparedStatement preparedStatement =
             connection.prepareStatement(dateOption.equals(DateOption.MIN)
                 ? SELECT_MIN_TIMESTAMP
                 : SELECT_MAX_TIMESTAMP)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      result = Converter.convertTimestampToString(resultSet.getTimestamp("timestamp"));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
