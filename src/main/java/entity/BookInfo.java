// Package entity - This package contains entities retrieved from the database

package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// Entity representing Book Information
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class BookInfo {
  private String id;
  private String asin;
  private String guid;
  private String lang;
  private String title;
  private String authors;
}
