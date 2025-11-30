package chnu.edu.kn.rotar.booklibrary.service;

import chnu.edu.kn.rotar.booklibrary.model.Book;
import chnu.edu.kn.rotar.booklibrary.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return bookRepository.save(book);
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id).get();
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    public Book updateBook(Book book) {
        if (book.getId() == null) {
            return null;
        }
        return bookRepository.save(book);
    }
}
