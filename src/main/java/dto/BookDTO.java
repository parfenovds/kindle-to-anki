// Package dto - This package contains data transfer objects
package dto;

import lombok.Builder;
import lombok.Value;

// Data Transfer Object for Book information
@Builder
@Value
public class BookDTO {
  String author;
  String title;
}