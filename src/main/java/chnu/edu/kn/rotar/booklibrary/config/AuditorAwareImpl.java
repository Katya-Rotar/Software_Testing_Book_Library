package chnu.edu.kn.rotar.booklibrary.config;

/*
  @author   katya
  @project   BookLibrary
  @class  AuditorAwareImpl
  @version  1.0.0 
  @since 30.11.2025 - 18.04
*/

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(System.getProperty("user.name"));
    }
}
