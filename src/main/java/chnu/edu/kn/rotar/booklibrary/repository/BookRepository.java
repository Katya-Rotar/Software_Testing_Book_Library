package chnu.edu.kn.rotar.booklibrary.repository;

import chnu.edu.kn.rotar.booklibrary.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/*
  @author   katya
  @project   BookLibrary
  @class  BookRepository
  @version  1.0.0 
  @since 21.10.2025 - 20.41
*/

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
}
