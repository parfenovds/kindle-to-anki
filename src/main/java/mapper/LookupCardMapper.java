package mapper;

import entity.Card;
import entity.Lookup;
import java.util.HashSet;
import java.util.Set;

public enum LookupCardMapper implements Mapper<Lookup, Card> {
  INSTANCE;
  @Override
  public Card mapFrom(Lookup source) {
    String[] langToWord = source.getWordKey().split(":");
    return Card.builder()
        .word(langToWord[1])
        .sourceLanguage(langToWord[0])
        .originalSentence(source.getUsage().replaceAll("\u00A0", "").trim())
        .timestamp(source.getTimestamp())
        .build();
  }
  public Set<Card> toCardSet(Set<Lookup> lookups) {
    HashSet<Card> cards = new HashSet<>();
    for (Lookup lookup : lookups) {
      cards.add(mapFrom(lookup));
    }
    return cards;
  }
}
