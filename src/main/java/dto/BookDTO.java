package dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class BookDTO {
  String author;
  String title;
}
