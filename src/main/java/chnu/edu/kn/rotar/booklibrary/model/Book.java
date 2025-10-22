package chnu.edu.kn.rotar.booklibrary.model;

/*
  @author   katya
  @project   BookLibrary
  @class  Book
  @version  1.0.0 
  @since 21.10.2025 - 12.41
*/

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private int year;
    private String genre;

    public Book(String title, String author, int year, String genre) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Book book)) return false;

        return getId().equals(book.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
