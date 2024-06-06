// Package service - This package contains the main business logic

package service;

import dto.BookDTO;
import java.util.List;
import repository.BookRepository;

// Service for managing Book information
public enum BookInfoService {
  INSTANCE;  // Singleton pattern instance

  private final BookRepository bookRepository = BookRepository.INSTANCE;

  // Method to get all books from the repository
  public List<BookDTO> getAll() {
    return bookRepository.getAll();
  }
}
