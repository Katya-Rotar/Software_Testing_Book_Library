package chnu.edu.kn.rotar.booklibrary;

import chnu.edu.kn.rotar.booklibrary.request.BookUpdateRequest;
import chnu.edu.kn.rotar.booklibrary.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import chnu.edu.kn.rotar.booklibrary.model.Book;
import chnu.edu.kn.rotar.booklibrary.repository.BookRepository;
import chnu.edu.kn.rotar.booklibrary.request.BookCreateRequest;

/*
  @author   katya
  @project   BookLibrary
  @class  IntegrationTests
  @version  1.0.0 
  @since 01.12.2025 - 13.38
*/
@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository repository;

    private List<Book> books = new ArrayList<>();

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        books.clear();

        books.add(new Book("To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test"));
        books.add(new Book("1984", "George Orwell", 1949, "Dystopian###test"));
        books.add(new Book("Pride and Prejudice", "Jane Austen", 1813, "Romance###test"));
        repository.saveAll(books);
    }


    @Test
    void whenBookTitleIsUnique_thenCreateNewBook() throws Exception {
        // given
        BookCreateRequest request = new BookCreateRequest(
                "Fahrenheit 451",
                "Ray Bradbury",
                1953,
                "Sci-Fi"
        );

        // when
        ResultActions perform = mockMvc.perform(
                post("/api/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(request))
        );

        // then
        Book book = repository.findAll().stream()
                .filter(b -> b.getTitle().equals(request.title()))
                .findFirst()
                .orElse(null);

        perform.andExpect(status().is(200));

        assertNotNull(book);
        assertNotNull(book.getId());
        assertThat(book.getId()).isNotEmpty();
        assertThat(book.getTitle()).isEqualTo(request.title());
        assertThat(book.getAuthor()).isEqualTo(request.author());
        assertThat(book.getYear()).isEqualTo(request.year());
        assertThat(book.getGenre()).isEqualTo(request.genre());
        assertNotNull(book.getLastModifiedDate());
        assertNotNull(book.getCreatedDate());
    }

    @Test
    void whenUpdateBookWithValidId_thenBookIsUpdated() throws Exception {
        Book existing = repository.findAll().get(0);
        BookUpdateRequest request = new BookUpdateRequest(
                existing.getId(),
                existing.getTitle() + " Updated",
                existing.getAuthor(),
                existing.getYear(),
                existing.getGenre()
        );

        ResultActions perform = mockMvc.perform(
                put("/api/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(request))
        );

        perform.andExpect(status().isOk());

        Book updated = repository.findById(existing.getId()).orElse(null);
        assertNotNull(updated);
        assertThat(updated.getTitle()).isEqualTo(request.title());
    }

    @Test
    void whenUpdateBookWithInvalidId_thenReturnNull() throws Exception {
        BookUpdateRequest request = new BookUpdateRequest(
                "nonexistentid1234567890",
                "Some Title",
                "Some Author",
                2025,
                "TestGenre"
        );

        ResultActions perform = mockMvc.perform(
                put("/api/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(request))
        );

        perform.andExpect(status().isOk());
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void whenGetBookById_thenReturnBook() throws Exception {
        Book existing = repository.findAll().get(1);
        ResultActions perform = mockMvc.perform(
                get("/api/books/" + existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        perform.andExpect(status().isOk());
        Book book = repository.findById(existing.getId()).orElse(null);
        assertNotNull(book);
        assertThat(book.getTitle()).isEqualTo(existing.getTitle());
    }

    @Test
    void whenDeleteBookById_thenBookIsRemoved() throws Exception {
        Book existing = repository.findAll().get(2);
        ResultActions perform = mockMvc.perform(
                delete("/api/books/" + existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        perform.andExpect(status().isOk());
        Optional<Book> deleted = repository.findById(existing.getId());
        assertFalse(deleted.isPresent());
        assertThat(repository.count()).isEqualTo(2);
    }
}
