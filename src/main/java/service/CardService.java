package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import constant.Constants;
import dto.CardLibretranslateDTO;
import dto.TranslationResponseLibretranslateDTO;
import entity.Card;
import entity.Lookup;
import exception.ExceptionHandler;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import mapper.CardLibretranslateDTOMapper;
import mapper.LookupCardMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

public enum CardService {
  INSTANCE;
  private final LookupService lookupService = LookupService.INSTANCE;
  private final LookupCardMapper lookupCardMapper = LookupCardMapper.INSTANCE;
  private final OkHttpClient httpClient = new OkHttpClient();

  public Set<Card> getFilteredTranslated(String dateFrom, String dateTo, String sourceLanguage, String bookTitle, String targetLanguage) {
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
    try (ExecutorService executorService = Executors.newFixedThreadPool(1)) {
      futures = rawFiltered.stream()
          .map(card -> CompletableFuture.supplyAsync(() -> translate(card), executorService))
          .collect(Collectors.toSet());
    }
    return futures.stream().map(CompletableFuture::join).collect(Collectors.toSet());
  }

  public Card translate(Card card) {
    CardLibretranslateDTO cardLibreTranslateDTO = CardLibretranslateDTOMapper.INSTANCE.mapFrom(card);
    ObjectMapper objectMapper = new ObjectMapper();
    String url = Constants.LIBRETRANSLATE_URL;
    String json = "";

    try {
      json = objectMapper.writeValueAsString(cardLibreTranslateDTO);
      System.out.println("Request JSON: " + json);
    } catch (IOException e) {
      ExceptionHandler.handleException(e);
    }

    RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
    Request request = new Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .post(body)
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      System.out.println("Response Code: " + response.code());
      String responseBody = response.body().string();
      System.out.println("Response Body: " + responseBody);
      TranslationResponseLibretranslateDTO translate = objectMapper.readValue(
          responseBody,
          TranslationResponseLibretranslateDTO.class
      );
      card.setTranslatedSentence(translate.getTranslatedText());
    } catch (IOException e) {
      ExceptionHandler.handleException(e);
    }
    return card;
  }
}
