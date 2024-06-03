package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CardLibretranslateDTO;
import dto.TranslationResponseLibretranslateDTO;
import entity.Card;
import entity.Lookup;
import java.io.IOException;
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
    return getTranslated(rawFiltered);
  }

  private Set<Card> prepareCards(String dateFrom, String dateTo, String sourceLanguage, String bookTitle, String targetLanguage) {
    Set<Lookup> lookups = lookupService.getFiltered(dateFrom, dateTo, sourceLanguage, bookTitle);
    Set<Card> cardSet = lookupCardMapper.toCardSet(lookups);
    return injectTargetLanguage(cardSet, targetLanguage);
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

//  private Card translate(Card card) {
//    CardLibretranslateDTO cardLibreTranslateDTO = CardLibretranslateDTOMapper.INSTANCE.mapFrom(card);
//    ObjectMapper objectMapper = new ObjectMapper();
//    String url = "http://localhost:5000/translate";
//    String json = "";
//    try {
//      json = objectMapper.writeValueAsString(cardLibreTranslateDTO);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//      HttpPost httpPost = new HttpPost(url);
//      httpPost.setHeader("Content-Type", "application/json");
//      httpPost.setHeader("Accept", "application/json");
//      httpPost.setHeader("User-Agent", "PostmanRuntime/7.29.4");
//      httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
//      httpPost.setHeader("Connection", "keep-alive");
//      httpPost.setEntity(new StringEntity(json));
//      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//        System.out.println("Response Code: " + response.getCode());
//        System.out.println(response.getEntity());
//        TranslationResponseLibretranslateDTO translate = objectMapper.readValue(
//            response.getEntity().getContent(),
//            TranslationResponseLibretranslateDTO.class
//        );
//        card.setTranslatedSentence(translate.getTranslatedText());
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return card;
//  }

  public Card translate(Card card) {
    CardLibretranslateDTO cardLibreTranslateDTO = CardLibretranslateDTOMapper.INSTANCE.mapFrom(card);
    ObjectMapper objectMapper = new ObjectMapper();
    String url = "http://localhost:5000/translate";
    String json = "";

    try {
      json = objectMapper.writeValueAsString(cardLibreTranslateDTO);
      System.out.println("Request JSON: " + json);
    } catch (IOException e) {
      e.printStackTrace();
    }

    RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
    Request request = new Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .post(body)
        .build();

    // Логирование всех заголовков запроса
    System.out.println("Request Headers:");
    for (String name : request.headers().names()) {
      System.out.println(name + ": " + request.header(name));
    }

    try (Response response = httpClient.newCall(request).execute()) {
      System.out.println("Response Code: " + response.code());
      String responseBody = response.body().string();
      System.out.println("Response Body: " + responseBody);

      if (response.code() == 400) {
        System.out.println("Bad Request: " + responseBody);
        return null;
      }

      TranslationResponseLibretranslateDTO translate = objectMapper.readValue(
          responseBody,
          TranslationResponseLibretranslateDTO.class
      );
      card.setTranslatedSentence(translate.getTranslatedText());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return card;
  }
}
