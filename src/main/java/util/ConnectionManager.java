package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ExceptionHandler;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@Log4j2
public class ConnectionManager {
  private final OkHttpClient httpClient = new OkHttpClient();

  public <T> T proceedPOST(String json, String url, Class<T> clazz) {
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = ConnectionManager.prepareRequest(json, url);
    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = ConnectionManager.getResponseBody(response);
      if (response.code() == 200) {
        return getReceivedDTO(clazz, objectMapper, responseBody);
      } else {
        ConnectionManager.handleBadAnswers(objectMapper, responseBody, response);
      }
    } catch (IOException e) {
      ExceptionHandler.handleException(e);
    }
    return null;
  }

  private static <T> T getReceivedDTO(Class<T> clazz, ObjectMapper objectMapper, String responseBody) throws JsonProcessingException {
    return objectMapper.readValue(responseBody, clazz);
  }

  @NotNull
  public static Request prepareRequest(String json, String url) {
    RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
    Request request = new Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .post(body)
        .build();
    return request;
  }

  @NotNull
  public static String getResponseBody(Response response) throws IOException {
    log.debug("Response Code: {}", response.code());
    String responseBody = response.body().string();
    log.debug("Response Body: {}", responseBody);
    return responseBody;
  }

  public static void handleBadAnswers(ObjectMapper objectMapper, String responseBody, Response response) throws JsonProcessingException {
    JsonNode responseJson = objectMapper.readTree(responseBody);
    String errorMessage;
    if (responseJson.has("error")) {
      errorMessage = "Server Error: " + responseJson.get("error").asText();
    } else {
      errorMessage = "Unexpected server response: " + responseBody;
    }
    ExceptionHandler.handleException(new RuntimeException(String.format(
        "Server returned error code: %d with message %s",
        response.code(),
        errorMessage
    )));
  }

  public <T> T proceedGET(String url, TypeReference<T> typeReference) {
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = prepareGetRequest(url);
    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = getResponseBody(response);
      if (response.code() == 200) {
        return getReceivedDTO(objectMapper, responseBody, typeReference);
      } else {
        handleBadAnswers(objectMapper, responseBody, response);
      }
    } catch (IOException e) {
      ExceptionHandler.handleException(e);
    }
    return null;
  }

  private <T> T getReceivedDTO(ObjectMapper objectMapper, String responseBody, TypeReference<T> typeReference) throws IOException {
    return objectMapper.readValue(responseBody, typeReference);
  }

  @NotNull
  private Request prepareGetRequest(String url) {
    return new Request.Builder()
        .url(url)
        .addHeader("Accept", "application/json")
        .get()
        .build();
  }
}
