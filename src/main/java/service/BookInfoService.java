package service;

import dto.BookDTO;
import java.util.List;
import repository.BookRepository;

public enum BookInfoService {
  INSTANCE;
  private final BookRepository bookRepository = BookRepository.INSTANCE;
  public List<BookDTO> getAll() {
    return bookRepository.getAll();
  }
}
