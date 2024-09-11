package dev.kukukodes.KDAP.Auth.configurations.database;

import dev.kukukodes.KDAP.Auth.database.tableQueryDialect.TableQueryDialectAdapter;
import dev.kukukodes.KDAP.Auth.database.tableQueryDialect.TableQueryDialectGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {

    @Autowired
    TableQueryDialectAdapter tableQueryDialectAdapter;

    @Bean
    TableQueryDialectGenerator tableQueryDialectGenerator() {
        return new TableQueryDialectGenerator(tableQueryDialectAdapter);
    }

}
