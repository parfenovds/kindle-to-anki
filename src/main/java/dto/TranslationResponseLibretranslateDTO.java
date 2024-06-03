package dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
public class TranslationResponseLibretranslateDTO {
  private String translatedText;
  @JsonCreator
  public TranslationResponseLibretranslateDTO(@JsonProperty("translatedText") String translatedText) {
    this.translatedText = translatedText;
  }
}
