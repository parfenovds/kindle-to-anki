package dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerErrorResponseDTO {
  private String error;
  @JsonCreator
  public ServerErrorResponseDTO(@JsonProperty("error") String error) {
    this.error = error;
  }
}
