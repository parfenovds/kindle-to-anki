package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
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

// Service for managing Cards and processing CSV generation
@Log4j2
public enum CardService {
  INSTANCE;  // Singleton pattern instance

  private final LookupService lookupService = LookupService.INSTANCE;  // Lookup service instance for retrieving lookups
  private final LookupCardMapper lookupCardMapper = LookupCardMapper.INSTANCE;  // Mapper for converting lookups to cards

  // Main method for processing cards and generating the CSV file
  public void cardProceeding(
      String dateFrom,
      String dateTo,
      String sourceLanguage,
      String targetLanguage,
      Integer limit,
      String timestamp) {
    // Get the filtered and translated cards based on the given parameters
    Set<Card> filteredTranslated = getFilteredTranslated(dateFrom, dateTo, sourceLanguage, targetLanguage, limit, timestamp);
    // Export the cards to a CSV file
    exportToCsv(filteredTranslated, PathsHandler.getOutputFileAddress());
  }

  // Method to get filtered and translated cards
  public Set<Card> getFilteredTranslated(
      String dateFrom,
      String dateTo,
      String sourceLanguage,
      String targetLanguage,
      Integer limit,
      String timestamp) {
    // Prepare the cards by filtering based on the provided parameters
    Set<Card> rawFiltered = prepareCards(dateFrom, dateTo, sourceLanguage, targetLanguage, limit, timestamp);
    // Translate the filtered cards using the translation service
    Set<Card> translated = getTranslated(rawFiltered);
    // Make key words bold in the translated cards for emphasis
    return makeKeyWordsBold(translated);
  }

  // Method to make key words bold in the original sentence of each card
  private Set<Card> makeKeyWordsBold(Set<Card> cards) {
    for (Card card : cards) {
      String originalSentence = card.getOriginalSentence();
      for (String word : card.getWords()) {
        String regex = "\\b" + word + "\\b";  // Regular expression to match whole words
        originalSentence = originalSentence.replaceAll(regex, "<b>" + word + "</b>");  // Replace matched words with bold tags
      }
      card.setOriginalSentence(originalSentence);  // Update the original sentence with bold words
    }
    return cards;
  }

  // Method to prepare cards by retrieving and mapping lookups, then setting the target language
  private Set<Card> prepareCards(String dateFrom, String dateTo, String sourceLanguage, String targetLanguage, Integer limit, String timestamp) {
    // Get filtered lookups from LookupService based on the provided parameters
    Set<Lookup> lookups = lookupService.getFiltered(dateFrom, dateTo, sourceLanguage, limit, timestamp);
    // Map lookups to cards and collect words for each card
    Set<Card> cardSet = toCardSet(lookups);
    // Inject the target language into each card
    return injectTargetLanguage(cardSet, targetLanguage);
  }

  // Method to map lookups to cards and collect words for each card
  private Set<Card> toCardSet(Set<Lookup> lookups) {
    HashMap<Card, Set<String>> cardsToWords = new HashMap<>();
    for (Lookup lookup : lookups) {
      Card card = lookupCardMapper.mapFrom(lookup);  // Map lookup to card
      cardsToWords.putIfAbsent(card, new HashSet<>());  // Add new card if not already present
      cardsToWords.get(card).add(lookup.getWordKey().split(":")[1]);  // Add the word from the lookup to the card's words
    }
    cardsToWords.forEach(Card::setWords);  // Set words for each card
    return cardsToWords.keySet();  // Return the set of cards
  }

  // Method to set the target language for each card in the set
  private Set<Card> injectTargetLanguage(Set<Card> cardSet, String targetLanguage) {
    cardSet.forEach(card -> card.setTargetLanguage(targetLanguage));  // Set the target language for each card
    return cardSet;
  }

  // Method to translate the original sentences of the filtered cards
  private Set<Card> getTranslated(Set<Card> rawFiltered) {
    Set<CompletableFuture<Card>> futures;
    ExecutorService executorService = Executors.newFixedThreadPool(1);  // Create a thread pool with a single thread
    String libreAddress = PathsHandler.getLibreAddressHolder();  // Get the address of the translation service
    futures = rawFiltered.stream()
        .map(card -> CompletableFuture.supplyAsync(() -> translate(card, libreAddress), executorService))  // Asynchronously translate each card
        .collect(Collectors.toSet());  // Collect the futures into a set
    return futures.stream().map(CompletableFuture::join).collect(Collectors.toSet());  // Wait for all translations to complete and collect the results
  }

  // Method to translate the original sentence in the card using the translation service
  public Card translate(Card card, String address) {
    log.info("Requesting translation for {}", card.getOriginalSentence());
    CardLibretranslateDTO cardLibreTranslateDTO = CardLibretranslateDTOMapper.INSTANCE.mapFrom(card);  // Map the card to a DTO for the translation service
    TranslationResponseLibretranslateDTO translate = ConnectionManager.proceedPOST(
        prepareJson(cardLibreTranslateDTO),  // Prepare the JSON request body
        address + "/translate",  // URL of the translation service
        TranslationResponseLibretranslateDTO.class  // Response type
    );
    log.info("Translation for {} is {}", card.getOriginalSentence(), translate.getTranslatedText());
    card.setTranslatedSentence(translate.getTranslatedText());  // Set the translated sentence in the card
    return card;
  }

  // Method to convert CardLibretranslateDTO to JSON
  private static String prepareJson(CardLibretranslateDTO cardLibreTranslateDTO) {
    String json = "";
    ObjectMapper objectMapper = new ObjectMapper();  // Create a new ObjectMapper for JSON serialization
    try {
      json = objectMapper.writeValueAsString(cardLibreTranslateDTO);  // Serialize the DTO to JSON
      log.debug("Request: {}", json);
    } catch (IOException e) {
      ExceptionHandler.handleException(e);  // Handle JSON processing exceptions
    }
    return json;
  }

  // Method to export the cards to a CSV file
  public void exportToCsv(Set<Card> cards, String filePath) {
    if (cards.isEmpty()) {
      log.info("No entries found");  // Log and return if there are no cards to export
      return;
    }
    long ts = 0;
    try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {  // Open CSV writer
      for (Card card : cards) {
        long time = card.getTimestamp().getTime();
        if (time > ts) {
          ts = time;  // Update the latest timestamp
        }
        String[] data = {card.getTranslatedSentence(), card.getOriginalSentence()};
        writer.writeNext(data);  // Write the card data to the CSV file
      }
      log.info("The file successfully written at {}", filePath);
      log.info("Next time, to start from the next position (after the last received entry), use the -P tag with this number: {}", ts + 1L);
    } catch (IOException e) {
      ExceptionHandler.handleException(e);  // Handle IO exceptions
    }
  }
}
