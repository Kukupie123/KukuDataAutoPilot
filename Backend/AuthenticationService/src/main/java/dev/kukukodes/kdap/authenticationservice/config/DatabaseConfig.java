package dev.kukukodes.kdap.authenticationservice.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
public class DatabaseConfig {

    /**
     * Runs commands defined in the file during initialization of database
     */
    @Bean
    public ConnectionFactoryInitializer connectionFactoryInitializer( ConnectionFactory connectionFactory) {
        var cfi = new ConnectionFactoryInitializer();
        cfi.setConnectionFactory(connectionFactory);
        var script = new ClassPathResource("schema_postgresql.sql");
        cfi.setDatabasePopulator(new ResourceDatabasePopulator(script));
        return cfi;
    }
}
