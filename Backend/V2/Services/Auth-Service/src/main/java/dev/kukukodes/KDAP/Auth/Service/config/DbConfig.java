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
    @Profile("test")
    public ConnectionFactory h2connectionFactory() {
        log.info("Creating TEST H2 Connection Factory");
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "h2")
                .option(ConnectionFactoryOptions.PROTOCOL, "file") // In-memory database
                .option(ConnectionFactoryOptions.DATABASE, "./testDb") // Database name
                .option(ConnectionFactoryOptions.USER, "kuku")
                .option(ConnectionFactoryOptions.PASSWORD, "kuku")
                .build();
        return ConnectionFactories.get(options);
    }

    @Bean
    @Profile("dev")
    public ConnectionFactory connectionFactory() {
        log.info("Creating DEV H2 Connection Factory");
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "h2")
                .option(ConnectionFactoryOptions.PROTOCOL, "file") // In-memory database
                .option(ConnectionFactoryOptions.DATABASE, "./devDb") // Database name
                .option(ConnectionFactoryOptions.USER, "kuku")
                .option(ConnectionFactoryOptions.PASSWORD, "kuku")
                .build();
        return ConnectionFactories.get(options);
    }


}
