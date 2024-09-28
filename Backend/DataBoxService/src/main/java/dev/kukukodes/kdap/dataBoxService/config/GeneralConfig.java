package dev.kukukodes.kdap.dataBoxService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class GeneralConfig {

    @Bean
    public ObjectMapper parser() {
        return JsonMapper.builder().addModule(new JavaTimeModule()).build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
