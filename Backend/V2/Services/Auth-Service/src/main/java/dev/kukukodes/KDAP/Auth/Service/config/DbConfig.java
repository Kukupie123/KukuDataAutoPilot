package dev.kukukodes.KDAP.Auth.Service.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DbConfig {
    Logger log = LoggerFactory.getLogger(DbConfig.class);

    @Bean
    @Profile("test") //This bean will only be used in test profile
    public ConnectionFactory h2connectionFactory() {
        log.info("Creating TEST H2 Connection Factory");
        // Define the connection factory options
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder().option(ConnectionFactoryOptions.DRIVER, "h2")
                .option(ConnectionFactoryOptions.PROTOCOL, "mem") // In-memory database
                .option(ConnectionFactoryOptions.DATABASE, "testdb") // Database name
                .option(ConnectionFactoryOptions.USER, "kuku")
                .option(ConnectionFactoryOptions.PASSWORD, "kuku")
                .build();

        // Return the connection factory
        return ConnectionFactories.get(options);
    }

    @Bean
    @Profile("dev") //This bean will only be used in dev profile
    public ConnectionFactory connectionFactory() {
        log.info("Creating DEV H2 Connection Factory");
        // Define the connection factory options
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder().option(ConnectionFactoryOptions.DRIVER, "h2")
                .option(ConnectionFactoryOptions.PROTOCOL, "mem") // In-memory database
                .option(ConnectionFactoryOptions.DATABASE, "devDb") // Database name
                .option(ConnectionFactoryOptions.USER, "kuku")
                .option(ConnectionFactoryOptions.PASSWORD, "kuku")
                .build();

        // Return the connection factory
        return ConnectionFactories.get(options);
    }
}
