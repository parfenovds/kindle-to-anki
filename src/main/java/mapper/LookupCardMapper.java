package mapper;

import entity.Card;
import entity.Lookup;
import java.util.HashSet;

public enum LookupCardMapper implements Mapper<Lookup, Card> {
  INSTANCE;
  @Override
  public Card mapFrom(Lookup source) {
    String[] langToWord = source.getWordKey().split(":");
    return Card.builder()
        .words(new HashSet<>())
        .sourceLanguage(langToWord[0])
        .originalSentence(source.getUsage().replaceAll("\u00A0", "").trim())
        .timestamp(source.getTimestamp())
        .build();
  }
}
