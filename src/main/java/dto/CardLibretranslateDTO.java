package dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CardLibretranslateDTO {
  String q;
  String source;
  String target;
}
