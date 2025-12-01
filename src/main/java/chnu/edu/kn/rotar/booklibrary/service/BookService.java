package chnu.edu.kn.rotar.booklibrary.service;

import chnu.edu.kn.rotar.booklibrary.model.Book;
import chnu.edu.kn.rotar.booklibrary.repository.BookRepository;
import chnu.edu.kn.rotar.booklibrary.request.BookCreateRequest;
import chnu.edu.kn.rotar.booklibrary.request.BookUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
  @author   katya
  @project   BookLibrary
  @class  BookService
  @version  1.0.0 
  @since 21.10.2025 - 12.53
*/
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private List<Book> books = new ArrayList<>(
            Arrays.asList(
                    new Book("1", "To Kill a Mockingbird", "Harper Lee", 1960, "Classic"),
                    new Book("2", "1984", "George Orwell", 1949, "Dystopian"),
                    new Book("3", "Pride and Prejudice", "Jane Austen", 1813, "Romance")
            )
    );

    @PostConstruct
    void init() {
        bookRepository.deleteAll();
        bookRepository.saveAll(books);
    }


    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public List<Book> createAll(List<Book> items) {
        return bookRepository.saveAll(items);
    }

    public Book addBook(Book book) {
        if (book.getId() != null && bookRepository.existsById(book.getId()) ) {
            return null;
        }
        return bookRepository.save(book);
    }

    public Book addBook(BookCreateRequest request) {

        if (bookRepository.existsByTitle(request.title())) {
            throw new IllegalStateException("Book with same title already exists");
        }

        Book book = mapToBook(request);
        book.setCreatedDate(LocalDateTime.now());
        book.setLastModifiedDate(null);

        return bookRepository.save(book);
    }

    private Book mapToBook(BookCreateRequest request) {
        return new Book(request.title(), request.author(), request.year(), request.genre());
    }

    public Book updateBook(BookUpdateRequest request) {
        Book persisted = bookRepository.findById(request.id()).orElse(null);

        if (persisted != null) {
            Book book = Book.builder()
                    .id(request.id())
                    .title(request.title())
                    .author(request.author())
                    .year(request.year())
                    .genre(request.genre())
                    .createdDate(persisted.getCreatedDate())
                    .lastModifiedDate(LocalDateTime.now())
                    .build();

            return bookRepository.save(book);
        }
        return null;
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id).get();
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    public Book updateBook(Book book) {
        if (book.getId() == null || !bookRepository.existsById(book.getId())) {
            return null;
        }
        return bookRepository.save(book);
    }
}
