package chnu.edu.kn.rotar.booklibrary.request;

/*
  @author   katya
  @project   BookLibrary
  @class  BookUpdateRequest
  @version  1.0.0 
  @since 01.12.2025 - 14.43
*/
public record BookUpdateRequest(String id, String title, String author, int year, String genre) {
}
