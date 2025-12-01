package chnu.edu.kn.rotar.booklibrary.request;

/*
  @author   katya
  @project   BookLibrary
  @class  BookCreateRequest
  @version  1.0.0 
  @since 01.12.2025 - 14.40
*/
public record BookCreateRequest(String title, String author, int year, String genre) {
}
