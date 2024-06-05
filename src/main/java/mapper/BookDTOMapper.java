package mapper;

import dto.BookDTO;
import entity.BookInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum BookDTOMapper implements Mapper<BookInfo, BookDTO> {
  INSTANCE;
  @Override
  public BookDTO mapFrom(BookInfo source) {
    return BookDTO.builder()
        .author(source.getAuthors())
        .title(source.getTitle())
        .build();
  }

  public List<BookDTO> mapAll(List<BookInfo> bookInfoSet) {
    return bookInfoSet.stream().map(this::mapFrom).toList();
  }
}
