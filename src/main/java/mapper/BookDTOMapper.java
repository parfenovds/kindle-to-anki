// Package mapper - This package contains object mappers for mapping between different objects

package mapper;

import dto.BookDTO;
import entity.BookInfo;
import java.util.List;

// Mapper for mapping BookInfo to BookDTO
public enum BookDTOMapper implements Mapper<BookInfo, BookDTO> {
  INSTANCE;  // Singleton pattern instance

  @Override
  public BookDTO mapFrom(BookInfo source) {
    // Map BookInfo to BookDTO
    return BookDTO.builder()
        .author(source.getAuthors())
        .title(source.getTitle())
        .build();
  }

  public List<BookDTO> mapAll(List<BookInfo> bookInfoSet) {
    // Map a list of BookInfo to a list of BookDTO
    return bookInfoSet.stream().map(this::mapFrom).toList();
  }
}
