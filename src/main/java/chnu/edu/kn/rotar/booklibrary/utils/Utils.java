package chnu.edu.kn.rotar.booklibrary.utils;

/*
  @author   katya
  @project   BookLibrary
  @class  Utils
  @version  1.0.0 
  @since 01.12.2025 - 15.14
*/
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    public static String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
