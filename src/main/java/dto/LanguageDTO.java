package dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

// Data Transfer Object for Language information
@Data
@Builder
@EqualsAndHashCode(of = "code")
public class LanguageDTO {
  private String code;
  private String name;
  private List<String> targets;

  @JsonCreator
  public LanguageDTO(
      @JsonProperty("code") String code,
      @JsonProperty("name") String name,
      @JsonProperty("targets") List<String> targets) {
    this.code = code;
    this.name = name;
    this.targets = targets;
  }
}