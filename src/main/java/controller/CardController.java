package controller;

import com.opencsv.CSVWriter;
import entity.Card;
import exception.ExceptionHandler;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import service.CardService;
import util.PathsHandler;

public enum CardController {
  INSTANCE;
  private final CardService cardService = CardService.INSTANCE;

  public void makeCSV(
      String dateFrom,
      String dateTo,
      String sourceLanguage,
      String bookTitle,
      String targetLanguage,
      String databaseAddress,
      String outputFilePath) {
    PathsHandler.setDatabaseAddress(databaseAddress);
    Set<Card> filteredTranslated = cardService.getFilteredTranslated(dateFrom, dateTo, sourceLanguage, bookTitle, targetLanguage);
    exportToCsv(filteredTranslated, outputFilePath);
  }
  private void exportToCsv(Set<Card> cards, String filePath) {
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
      for (Card card : cards) {
        String[] data = {card.getTranslatedSentence(), card.getOriginalSentence()};
        writer.writeNext(data);
      }
    } catch(IOException e) {
      ExceptionHandler.handleException(e);
    }
  }
}
