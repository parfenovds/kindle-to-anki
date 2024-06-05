package service;

import com.fasterxml.jackson.core.type.TypeReference;
import dto.LanguageDTO;
import exception.ExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
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
      ExceptionHandler.handleException(e);
    }
    return languages;
  }
}
