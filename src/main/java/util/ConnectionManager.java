package util;

// Utility class for connection management using OkHttp
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ExceptionHandler;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
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
  // OkHttp client configuration
  private final OkHttpClient httpClient = new OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build();

  public <T> T proceedPOST(String json, String url, Class<T> clazz) {
    // Prepare and execute HTTP POST request
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = ConnectionManager.prepareRequest(json, url);
    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = ConnectionManager.getResponseBody(response);
      if (response.code() == 200) {
        // Deserialize the response body to the specified class type
        return getReceivedDTO(clazz, objectMapper, responseBody);
      } else {
        // Handle bad responses from the server
        ConnectionManager.handleBadAnswers(objectMapper, responseBody, response);
      }
    } catch (IOException e) {
      // Handle exceptions
      ExceptionHandler.handleException(e);
    }
    return null;
  }

  private static <T> T getReceivedDTO(Class<T> clazz, ObjectMapper objectMapper, String responseBody) throws JsonProcessingException {
    // Deserialize the response body to the specified class type
    return objectMapper.readValue(responseBody, clazz);
  }

  @NotNull
  public static Request prepareRequest(String json, String url) {
    // Prepare HTTP POST request with JSON body
    RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
    return new Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .post(body)
        .build();
  }

  @NotNull
  public static String getResponseBody(Response response) throws IOException {
    // Get the response body as a string
    log.debug("Response Code: {}", response.code());
    String responseBody = response.body().string();
    log.debug("Response Body: {}", responseBody);
    return responseBody;
  }

  public static void handleBadAnswers(ObjectMapper objectMapper, String responseBody, Response response) throws JsonProcessingException {
    // Handle bad responses from the server
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
    // Prepare and execute HTTP GET request
    ObjectMapper objectMapper = new ObjectMapper();
    Request request = prepareGetRequest(url);
    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = getResponseBody(response);
      if (response.code() == 200) {
        // Deserialize the response body to the specified type reference
        return getReceivedDTO(objectMapper, responseBody, typeReference);
      } else {
        // Handle bad responses from the server
        handleBadAnswers(objectMapper, responseBody, response);
      }
    } catch (IOException e) {
      // Handle exceptions
      ExceptionHandler.handleException(e);
    }
    return null;
  }

  private <T> T getReceivedDTO(ObjectMapper objectMapper, String responseBody, TypeReference<T> typeReference) throws IOException {
    // Deserialize the response body to the specified type reference
    return objectMapper.readValue(responseBody, typeReference);
  }

  @NotNull
  private Request prepareGetRequest(String url) {
    // Prepare HTTP GET request
    return new Request.Builder()
        .url(url)
        .addHeader("Accept", "application/json")
        .get()
        .build();
  }
}
