package service;

import com.fasterxml.jackson.core.type.TypeReference;
import dto.LanguageDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import util.ConnectionManager;

@Log4j2
public enum LanguageService {
  INSTANCE;
  private final OkHttpClient httpClient = new OkHttpClient();

  public List<LanguageDTO> getLanguages() {
    List<LanguageDTO> languages = new ArrayList<>();
    String url = "http://localhost:5000/languages";
    try {
      languages = ConnectionManager.proceedGET(url, new TypeReference<>() {
      });
      if (languages != null) {
//        languages.forEach(System.out::println);
      } else {
        System.out.println("Failed to fetch language data.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return languages;
  }
}
