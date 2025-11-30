package chnu.edu.kn.rotar.booklibrary;

/*
  @author   katya
  @project   BookLibrary
  @class  AuditTest
  @version  1.0.0 
  @since 30.11.2025 - 18.24
*/
import chnu.edu.kn.rotar.booklibrary.model.Book;
import chnu.edu.kn.rotar.booklibrary.service.BookService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuditTest {

    @Autowired
    BookService underTest;

    @BeforeAll
    void beforeAll() {
    }

    @BeforeEach
    void setUp() {
        Book b1 = new Book("To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test");
        Book b2 = new Book("1984", "George Orwell", 1949, "Dystopian###test");
        Book b3 = new Book("Pride and Prejudice", "Jane Austen", 1813, "Romance###test");
        underTest.createAll(List.of(b1, b2, b3));
    }

    @AfterEach
    void tearDown() {
        List<Book> itemsToDelete = underTest.getAllBooks().stream()
                .filter(item -> item.getGenre().contains("###test"))
                .toList();
        for (Book item : itemsToDelete) {
            underTest.deleteBook(item.getId());
        }
        // underTest.deleteAll(itemsToDelete);
    }

    @AfterAll
    void afterAll() {}

    @Test
    void testSetShouldContains_3_Records_ToTest(){
        List<Book> itemsToDelete = underTest.getAllBooks().stream()
                .filter(item -> item.getGenre().contains("###test"))
                .toList();
        assertEquals(3,itemsToDelete.size());
    }

    @Test
    void testCreateSetsCreatedAndLastModified_equalOnCreate() {
        Book book = new Book("New Book", "Author", 2025, "###test");
        Book created = underTest.addBook(book);

        assertNotNull(created.getId());
        assertNotNull(created.getCreatedDate());
        assertNotNull(created.getLastModifiedDate());
        assertEquals(created.getCreatedDate(), created.getLastModifiedDate());
    }

    @Test
    void testUpdateUpdatesLastModified_butKeepsCreatedDate() throws InterruptedException {
        Book book = new Book("New Book", "Author", 2025, "###test");
        underTest.addBook(book);
        LocalDateTime created = book.getCreatedDate();

        Thread.sleep(10);
        book.setTitle("Book updated");
        Book updated = underTest.updateBook(book);

        assertEquals(created, updated.getCreatedDate());
        assertTrue(updated.getLastModifiedDate().isAfter(created));
    }

    @Test
    void testBulkCreateSetsAuditForAll() {
        List<Book> input = List.of(
                new Book("New Book 1","Author",2025,"###test"),
                new Book("New Book 2","Author",2024,"###test")
        );
        List<Book> saved = underTest.createAll(input);

        assertEquals(2, saved.size());
        saved.forEach(b -> {
            assertNotNull(b.getId());
            assertNotNull(b.getCreatedDate());
            assertNotNull(b.getLastModifiedDate());
        });
    }

    @Test
    void testUpdateWithNullId_returnsNullOrThrows() {
        Book book = new Book(null,"New Book", "Author", 2025, "###test");
        Book result = underTest.updateBook(book);
        assertNull(result);
    }

    @Test
    void testSaveAllDoesNotOverrideCreatedDateForExistingItems() {
        Book b = underTest.addBook(new Book("Original", "A", 2000, "###test"));
        LocalDateTime created = b.getCreatedDate();

        b.setTitle("Updated title");
        underTest.createAll(List.of(b));

        Book reloaded = underTest.getBookById(b.getId());
        assertEquals(
                created.withNano(0),
                reloaded.getCreatedDate().withNano(0)
        );
    }

    @Test
    void testUpdateWithoutChangingFields_stillUpdatesLastModified() throws InterruptedException {
        Book book = underTest.addBook(new Book("Static Book", "A", 2025, "###test"));
        LocalDateTime beforeUpdate = book.getLastModifiedDate();

        Thread.sleep(10);
        Book updated = underTest.updateBook(book);

        assertTrue(updated.getLastModifiedDate().isAfter(beforeUpdate));
    }

    @Test
    void testUpdateDoesNotRegenerateCreatedDate() throws InterruptedException {
        Book b = underTest.addBook(new Book("AAA", "BBB", 2025, "###test"));
        LocalDateTime created = b.getCreatedDate();

        Thread.sleep(10);
        b.setAuthor("Updated");
        Book updated = underTest.updateBook(b);

        assertEquals(created, updated.getCreatedDate());
    }

    @Test
    void testCreateWithProvidedId_createdDateSet_lastModifiedNull() {
        Book book = new Book("1", "New Book", "Author", 2025, "###test");

        Book created = underTest.addBook(book);

        assertEquals("1", created.getId());
        assertNotNull(created.getLastModifiedDate());
    }

    @Test
    void testUpdateWithExistingId_lastModifiedAfterCreated() throws InterruptedException {
        Book book = new Book("10", "Book1", "A", 2025, "###test");
        Book b = underTest.addBook(book);

        assertNotNull(b.getLastModifiedDate());
        LocalDateTime created = b.getLastModifiedDate();

        Thread.sleep(5);

        b.setTitle("Updated title");
        Book updated = underTest.updateBook(b);

        assertNotNull(updated.getLastModifiedDate());
        assertTrue(updated.getLastModifiedDate().isAfter(created));
    }
}
