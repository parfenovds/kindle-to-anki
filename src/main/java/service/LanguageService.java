package service;

import com.fasterxml.jackson.core.type.TypeReference;
import dto.LanguageDTO;
import exception.ExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import util.ConnectionManager;
import util.PathsHandler;

// Service for managing Language information
@Log4j2
public enum LanguageService {
  INSTANCE;  // Singleton pattern instance

  // Method to get the list of available languages from LibreTranslate API
  public List<LanguageDTO> getLanguages() {
    List<LanguageDTO> languages = new ArrayList<>();
    String url = PathsHandler.getLibreAddressHolder() + "/languages";  // Construct the URL for the languages endpoint
    try {
      languages = ConnectionManager.proceedGET(url, new TypeReference<>() {
      });  // Send GET request and parse the response
    } catch (Exception e) {
      ExceptionHandler.handleException(e);  // Handle any exceptions that occur during the request
    }
    return languages;
  }
}