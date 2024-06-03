package entity;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Card {
  private String word;
  private String originalSentence;
  private String translatedSentence;
  private Timestamp timestamp;
  private String sourceLanguage;
  private String targetLanguage;
}
