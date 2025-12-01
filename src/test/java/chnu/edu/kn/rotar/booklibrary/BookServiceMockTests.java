package chnu.edu.kn.rotar.booklibrary;

import chnu.edu.kn.rotar.booklibrary.model.Book;
import chnu.edu.kn.rotar.booklibrary.repository.BookRepository;
import chnu.edu.kn.rotar.booklibrary.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
  @author   katya
  @project   BookLibrary
  @class  BookServiceMockTests
  @version  1.0.0 
  @since 01.12.2025 - 08.10
*/
@ExtendWith(MockitoExtension.class)
public class BookServiceMockTests {
    @Mock
    private BookRepository mockRepository;

    @InjectMocks
    private BookService underTest;

    @Test
    void whenInsertNewBookAndSuchIdExistsThenFail() {

        Book bookToSave = new Book("1","To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test");
        given(mockRepository.existsById(bookToSave.getId())).willReturn(true);

        Book bookPersisted = underTest.addBook(bookToSave);

        then(mockRepository).should(never()).save(bookToSave);
        assertNull(bookPersisted);
        verify(mockRepository, never()).save(bookToSave);

        verify(mockRepository, times(0)).save(bookToSave);
        verify(mockRepository, times(1)).existsById(bookToSave.getId());
    }

    @Test
    void whenInsertNewBookAndSuchIdNotExistsThenOk() {

        Book bookToSave = new Book("1","To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test");
        given(mockRepository.existsById(bookToSave.getId())).willReturn(false);
        given(mockRepository.save(bookToSave)).willReturn(bookToSave);

        Book bookPersisted = underTest.addBook(bookToSave);

        then(mockRepository).should().save(bookToSave);

        assertNotNull(bookPersisted);

        assertEquals(bookToSave.getTitle(), bookPersisted.getTitle());

        verify(mockRepository, times(1)).save(bookToSave);
        verify(mockRepository, times(1)).existsById(bookToSave.getId());
    }

    @Test
    void whenUpdateBookAndIdIsNullThenFail() {
        Book book = new Book(null, "To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test");

        Book result = underTest.updateBook(book);

        assertNull(result);
        verify(mockRepository, never()).save(any());
    }

    @Test
    void whenUpdateBookAndSuchIdNotExistsThenFail() {
        Book book = new Book("1","To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test");
        given(mockRepository.existsById(book.getId())).willReturn(false);

        Book result = underTest.updateBook(book);

        assertNull(result);
        verify(mockRepository, never()).save(any());
        verify(mockRepository, times(1)).existsById(book.getId());
    }

    @Test
    void whenUpdateBookAndIdExistsThenOk() {
        Book book = new Book("1","To Kill a Mockingbird", "Harper Lee", 1960, "Classic###test");

        given(mockRepository.existsById(book.getId())).willReturn(true);
        given(mockRepository.save(book)).willReturn(book);

        Book updated = underTest.updateBook(book);

        assertNotNull(updated);
        assertEquals(book, updated);

        verify(mockRepository, times(1)).save(book);
        verify(mockRepository, times(1)).existsById(book.getId());
    }
}
