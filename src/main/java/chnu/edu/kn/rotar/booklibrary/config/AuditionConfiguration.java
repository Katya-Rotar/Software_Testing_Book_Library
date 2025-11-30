package chnu.edu.kn.rotar.booklibrary.config;

/*
  @author   katya
  @project   BookLibrary
  @class  AuditionConfiguration
  @version  1.0.0 
  @since 30.11.2025 - 18.03
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@Configuration
public class AuditionConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
}
