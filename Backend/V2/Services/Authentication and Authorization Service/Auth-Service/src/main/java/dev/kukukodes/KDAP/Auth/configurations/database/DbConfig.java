package dev.kukukodes.KDAP.Auth.configurations.database;

import dev.kukukodes.KDAP.Auth.database.tableQueryDialect.TableQueryDialectGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {

    @Bean
    TableQueryDialectGenerator tableQueryDialectGenerator() {
        return new TableQueryDialectGenerator();
    }

}
