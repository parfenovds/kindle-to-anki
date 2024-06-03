package service;

import constant.DateOption;
import entity.Lookup;
import java.util.Set;
import repository.LookupRepository;
import util.Converter;

public enum LookupService {
  INSTANCE;
  private final LookupRepository lookupRepository = LookupRepository.INSTANCE;

  public Set<Lookup> getFiltered(String dateFrom, String dateTo, String sourceLanguage, String bookTitle) {
    if(dateFrom == null) dateFrom = getDateForLimit(DateOption.MIN);
    if(dateTo == null) dateTo = getDateForLimit(DateOption.MAX);
    return lookupRepository.getFiltered(
        Converter.convertStringToTimestamp(dateFrom, true),
        Converter.convertStringToTimestamp(dateTo, false),
        sourceLanguage,
        bookTitle
    );
  }
  private String getDateForLimit(DateOption dateOption) {
    return lookupRepository.getDateForLimit(dateOption);
  }
}
