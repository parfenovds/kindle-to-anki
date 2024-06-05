package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import constant.Constants;
import dto.CardLibretranslateDTO;
import dto.TranslationResponseLibretranslateDTO;
import entity.Card;
import entity.Lookup;
import exception.ExceptionHandler;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import mapper.CardLibretranslateDTOMapper;
import mapper.LookupCardMapper;
import util.ConnectionManager;
import util.PathsHandler;

@Log4j2
public enum CardService {
  INSTANCE;
  private final LookupService lookupService = LookupService.INSTANCE;
  private final LookupCardMapper lookupCardMapper = LookupCardMapper.INSTANCE;

  public void cardProceeding(
      String dateFrom,
      String dateTo,
      String sourceLanguage,
      String bookTitle,
      String targetLanguage,
      String databaseAddress,
      String outputFilePath) {
    PathsHandler.setDatabaseAddress(databaseAddress);
    PathsHandler.setOutputFileAddress(outputFilePath);
    Set<Card> filteredTranslated = getFilteredTranslated(dateFrom, dateTo, sourceLanguage, bookTitle, targetLanguage);
    exportToCsv(filteredTranslated, PathsHandler.getOutputFileAddress());
  }

  public Set<Card> getFilteredTranslated(String dateFrom, String dateTo, String sourceLanguage, String bookTitle, String targetLanguage) {
    log.info("hello");
    Set<Card> rawFiltered = prepareCards(dateFrom, dateTo, sourceLanguage, bookTitle, targetLanguage);
    Set<Card> translated = getTranslated(rawFiltered);
    return makeKeyWordsBold(translated);
  }

  private Set<Card> makeKeyWordsBold(Set<Card> cards) {
    for (Card card : cards) {
      String originalSentence = card.getOriginalSentence();
      for (String word : card.getWords()) {
        String regex = "\\b" + word + "\\b";
        originalSentence = originalSentence.replaceAll(regex, "<b>" + word + "</b>");
      }
      card.setOriginalSentence(originalSentence);
    }
    return cards;
  }

  private Set<Card> prepareCards(String dateFrom, String dateTo, String sourceLanguage, String bookTitle, String targetLanguage) {
    Set<Lookup> lookups = lookupService.getFiltered(dateFrom, dateTo, sourceLanguage, bookTitle);
    Set<Card> cardSet = toCardSet(lookups);
    return injectTargetLanguage(cardSet, targetLanguage);
  }

  private Set<Card> toCardSet(Set<Lookup> lookups) {
    HashMap<Card, Set<String>> cardsToWords = new HashMap<>();
    for (Lookup lookup : lookups) {
      Card card = lookupCardMapper.mapFrom(lookup);
      cardsToWords.putIfAbsent(card, new HashSet<>());
      cardsToWords.get(card).add(lookup.getWordKey().split(":")[1]);
    }
    cardsToWords.forEach(Card::setWords);
    return cardsToWords.keySet();
  }

  private Set<Card> injectTargetLanguage(Set<Card> cardSet, String targetLanguage) {
    cardSet.forEach(card -> card.setTargetLanguage(targetLanguage));
    return cardSet;
  }

  private Set<Card> getTranslated(Set<Card> rawFiltered) {
    Set<CompletableFuture<Card>> futures;
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    futures = rawFiltered.stream()
        .map(card -> CompletableFuture.supplyAsync(() -> translate(card), executorService))
        .collect(Collectors.toSet());

    return futures.stream().map(CompletableFuture::join).collect(Collectors.toSet());
  }

  public Card translate(Card card) {
    log.info("Requesting translation for {}", card.getOriginalSentence());
    CardLibretranslateDTO cardLibreTranslateDTO = CardLibretranslateDTOMapper.INSTANCE.mapFrom(card);
    TranslationResponseLibretranslateDTO translate = ConnectionManager.proceedPOST(
        prepareJson(cardLibreTranslateDTO),
        Constants.LIBRETRANSLATE_URL,
        TranslationResponseLibretranslateDTO.class
    );
    log.info("Translation for {} is {}", card.getOriginalSentence(), translate.getTranslatedText());
    card.setTranslatedSentence(translate.getTranslatedText());
    return card;
  }

  private static String prepareJson(CardLibretranslateDTO cardLibreTranslateDTO) {
    String json = "";
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      json = objectMapper.writeValueAsString(cardLibreTranslateDTO);
      log.debug("Request: {}", json);
    } catch (IOException e) {
      ExceptionHandler.handleException(e);
    }
    return json;
  }

  public void exportToCsv(Set<Card> cards, String filePath) {
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
