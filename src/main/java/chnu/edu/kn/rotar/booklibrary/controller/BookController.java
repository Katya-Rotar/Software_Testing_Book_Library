package chnu.edu.kn.rotar.booklibrary.controller;

/*
  @author   katya
  @project   BookLibrary
  @class  BookController
  @version  1.0.0 
  @since 21.10.2025 - 12.46
*/

import chnu.edu.kn.rotar.booklibrary.model.Book;
import chnu.edu.kn.rotar.booklibrary.request.BookCreateRequest;
import chnu.edu.kn.rotar.booklibrary.request.BookUpdateRequest;
import chnu.edu.kn.rotar.booklibrary.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable String id) {
        return bookService.getBookById(id);
    }

    @PutMapping
    public Book updateBook(@RequestBody Book updatedBook) {
        return bookService.updateBook(updatedBook);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
    }

    //============== request =====================
    @PostMapping("/dto")
    public Book insert(@RequestBody BookCreateRequest request) {
        return bookService.addBook(request);
    }

    @PutMapping("/dto")
    public Book edit(@RequestBody BookUpdateRequest request) {
        return bookService.updateBook(request);
    }
}
