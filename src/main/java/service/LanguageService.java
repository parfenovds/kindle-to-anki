package service;

import com.fasterxml.jackson.core.type.TypeReference;
import dto.LanguageDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import util.ConnectionManager;
import util.PathsHandler;

@Log4j2
public enum LanguageService {
  INSTANCE;

  public List<LanguageDTO> getLanguages() {
    List<LanguageDTO> languages = new ArrayList<>();
    String url = PathsHandler.getLibreAddressHolder() + "/languages";
    try {
      languages = ConnectionManager.proceedGET(url, new TypeReference<>() {
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return languages;
  }
}