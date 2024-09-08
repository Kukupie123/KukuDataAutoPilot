package dev.kukukodes.KDAP.Auth.Service.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
@Configuration
public class DbConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        // Define the connection factory options
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "h2")
                .option(ConnectionFactoryOptions.PROTOCOL, "mem") // In-memory database
                .option(ConnectionFactoryOptions.DATABASE, "testdb") // Database name
                .option(ConnectionFactoryOptions.USER, "kuku")
                .option(ConnectionFactoryOptions.PASSWORD, "kuku")
                .build();

        // Return the connection factory
        return ConnectionFactories.get(options);
    }
}
