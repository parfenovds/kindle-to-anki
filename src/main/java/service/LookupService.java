package service;

import constant.DateOption;
import entity.Lookup;
import java.sql.Timestamp;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import repository.LookupRepository;
import util.Converter;

public enum LookupService {
  INSTANCE; // Singleton pattern instance

  // Instance of LookupRepository
  private final LookupRepository lookupRepository = LookupRepository.INSTANCE;

  // Method to get filtered lookups based on provided parameters
  public Set<Lookup> getFiltered(String dateFrom, String dateTo, String sourceLanguage, Integer limit, String timestamp) {
    // Convert date strings to timestamps
    Timestamp timestampFrom = getStartingTimestamp(dateFrom, timestamp);
    // If no end date is provided, set to maximum date
    if (dateTo == null) {
      dateTo = getDateForLimit(DateOption.MAX);
    }
    // If no limit is provided, set to maximum integer value
    if (limit == null) {
      limit = Integer.MAX_VALUE;
    }
    // Get filtered lookups from the repository
    return lookupRepository.getFiltered(
        timestampFrom,
        Converter.convertStringToTimestamp(dateTo, false),
        sourceLanguage,
        limit
    );
  }

  @Nullable
  private Timestamp getStartingTimestamp(String dateFrom, String timestamp) {
    // Determine the starting timestamp based on dateFrom or timestamp
    Timestamp timestampFrom;
    if (dateFrom == null && timestamp == null) {
      // If both dateFrom and timestamp are null, get minimum date
      dateFrom = getDateForLimit(DateOption.MIN);
      timestampFrom = Converter.convertStringToTimestamp(dateFrom, true);
    } else if (dateFrom != null && timestamp == null) {
      // If only dateFrom is provided, convert it to timestamp
      timestampFrom = Converter.convertStringToTimestamp(dateFrom, true);
    } else {
      // If timestamp is provided, convert it directly to Timestamp
      timestampFrom = new Timestamp(Long.parseLong(timestamp));
    }
    return timestampFrom;
  }

  private String getDateForLimit(DateOption dateOption) {
    // Get the date for the specified limit (MIN or MAX) from the repository
    return lookupRepository.getDateForLimit(dateOption);
  }
}
