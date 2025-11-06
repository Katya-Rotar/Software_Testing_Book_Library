package chnu.edu.kn.rotar.booklibrary;

import chnu.edu.kn.rotar.booklibrary.model.Book;
import chnu.edu.kn.rotar.booklibrary.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
  @author   katya
  @project   BookLibrary
  @class  BookRepositoryTest
  @version  1.0.0 
  @since 02.11.2025 - 12.34
*/

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class BookRepositoryTest {

    @Autowired
    BookRepository underTest;

    @BeforeAll
    void beforeAll() {
    }

    @BeforeEach
    void setUp() {
        Book b1 = new Book("1", "To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test");
        Book b2 = new Book("2", "1984", "George Orwell", 1949, "Dystopian###test");
        Book b3 = new Book("3", "Pride and Prejudice", "Jane Austen", 1813, "Romance###test");
        underTest.saveAll(List.of(b1, b2, b3));
    }

    @AfterEach
    void tearDown() {
        List<Book> booksToDelete = underTest.findAll().stream()
                .filter(b -> b.getGenre().contains("###test"))
                .toList();
        underTest.deleteAll(booksToDelete);
    }

    @AfterAll
    void afterAll() {
    }

    // 1 shouldSaveBookAndAssignId
    @Test
    void shouldSaveBookAndAssignId() {
        Book newBook = new Book(null, "The Hobbit", "J.R.R. Tolkien", 1937, "Fantasy###test");

        Book savedBook = underTest.save(newBook);
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertFalse(savedBook.getId().isEmpty());
        assertEquals(24, savedBook.getId().length());
    }

    // 2 shouldFindBookById
    @Test
    void shouldFindBookById() {
        Optional<Book> found = underTest.findById("1");

        assertTrue(found.isPresent());
        Book book = found.get();
        assertEquals("To Kill a Mockingbird", book.getTitle());
        assertEquals("Harper Lee", book.getAuthor());
        assertEquals(1960, book.getYear());
    }

    // 3 shouldDeleteBookById
    @Test
    void shouldDeleteBookById() {
        underTest.deleteById("2");
        Optional<Book> deleted = underTest.findById("2");
        assertFalse(deleted.isPresent());
    }

    // 4 shouldUpdateBookFields
    @Test
    void shouldUpdateBookFields() {
        Book book = underTest.findById("3").orElseThrow();
        book.setGenre("Updated###test");

        underTest.save(book);
        Book updated = underTest.findById("3").orElseThrow();
        assertEquals("Updated###test", updated.getGenre());
    }

    // 5 shouldReturnAllBooks
    @Test
    void shouldReturnAllBooks() {
        List<Book> allBooks = underTest.findAll().stream()
                .filter(b -> b.getGenre().contains("###test"))
                .toList();

        assertEquals(3, allBooks.size());
    }

    // 6 shouldNotFindNonExistingId
    @Test
    void shouldNotFindNonExistingId() {
        Optional<Book> found = underTest.findById("999");

        assertTrue(found.isEmpty());
    }

    // 7 shouldKeepCustomIdWhenProvided
    @Test
    void shouldKeepCustomIdWhenProvided() {
        Book book = new Book("custom123", "Animal Farm", "George Orwell", 1945, "Satire###test");

        Book saved = underTest.save(book);
        assertEquals("custom123", saved.getId());
    }

    // 8 shouldOverrideRecordWhenIdAlreadyExists
    @Test
    void shouldOverrideRecordWhenIdAlreadyExists() {
        Book existing = new Book("1", "Changed Title", "New Author", 2000, "Classic###test");

        underTest.save(existing);
        Book fromDb = underTest.findById("1").orElseThrow();

        assertEquals("Changed Title", fromDb.getTitle());
        assertEquals("New Author", fromDb.getAuthor());
    }

    // 9 shouldDeleteAllTestRecords
    @Test
    void shouldDeleteAllTestRecords() {
        List<Book> allBooks = underTest.findAll().stream()
                .filter(b -> b.getGenre().contains("###test"))
                .toList();

        underTest.deleteAll(allBooks);

        List<Book> remaining = underTest.findAll().stream()
                .filter(b -> b.getGenre().contains("###test"))
                .toList();

        assertTrue(remaining.isEmpty());
    }

    // 10 shouldDeleteOnlySpecificBook
    @Test
    void shouldDeleteOnlySpecificBook() {
        underTest.deleteById("3");

        List<Book> remaining = underTest.findAll().stream()
                .filter(b -> b.getGenre().contains("###test"))
                .toList();

        assertEquals(2, remaining.size());
        assertTrue(remaining.stream().noneMatch(b -> b.getId().equals("3")));
    }

    // 11 shouldSaveMultipleBooksAtOnce
    @Test
    void shouldSaveMultipleBooksAtOnce() {
        Book a = new Book(null, "Bulk A", "Author A", 2000, "Bulk###test");
        Book b = new Book(null, "Bulk B", "Author B", 2001, "Bulk###test");

        underTest.saveAll(List.of(a, b));

        List<Book> saved = underTest.findAll().stream()
                .filter(bk -> bk.getGenre().contains("Bulk###test"))
                .toList();

        assertEquals(2, saved.size());
    }

    // 12 shouldCheckIfBookExistsById
    @Test
    void shouldCheckIfBookExistsById() {
        assertTrue(underTest.existsById("1"));

        assertFalse(underTest.existsById("999"));
    }
}
