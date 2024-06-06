package mapper;

import entity.Card;
import entity.Lookup;
import java.util.HashSet;

// Mapper for mapping Lookup to Card
public enum LookupCardMapper implements Mapper<Lookup, Card> {
  INSTANCE;  // Singleton pattern instance

  @Override
  public Card mapFrom(Lookup source) {
    // Split wordKey to get the source language and the word
    String[] langToWord = source.getWordKey().split(":");
    // Map Lookup to Card
    return Card.builder()
        .words(new HashSet<>())
        .sourceLanguage(langToWord[0])
        .originalSentence(source.getUsage().replaceAll("\u00A0", "").trim())
        .timestamp(source.getTimestamp())
        .build();
  }
}
