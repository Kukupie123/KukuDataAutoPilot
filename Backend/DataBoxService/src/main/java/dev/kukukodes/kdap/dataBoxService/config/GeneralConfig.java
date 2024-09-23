package dev.kukukodes.kdap.dataBoxService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfig {
    @Bean
    public ObjectMapper parser() {
        return JsonMapper.builder().addModule(new JavaTimeModule()).build();
    }
}
