package dto;

import lombok.Builder;
import lombok.Value;

// Data Transfer Object for Card information to be used with LibreTranslate
@Value
@Builder
public class CardLibretranslateDTO {
  String q;
  String source;
  String target;
}
