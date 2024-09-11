package dev.kukukodes.KDAP.Auth.Service.db.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;


/**
 * Configuration class for setting up R2DBC database connections and initializing the database schema.
 *
 * <p>1. Defines the ConnectionFactory beans for different profiles (test and dev) to configure database connections.
 * 2. Defines the ConnectionFactoryInitializer bean for the test profile to initialize the database schema using an SQL script.
 */
@Configuration
@Slf4j
@Profile("test")
public class DbConfigTEST {


    @Bean
    R2dbcEntityTemplate template() {
        return new R2dbcEntityTemplate(testConnectionFactory());
    }

    /**
     * Creates a ConnectionFactory bean for the 'test' profile using an H2 database.
     *
     * @return ConnectionFactory configured for the test profile.
     */
    @Bean
    public ConnectionFactory testConnectionFactory() {
        log.info("Creating TEST H2 Connection Factory");
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "h2")
                .option(ConnectionFactoryOptions.PROTOCOL, "file") // Specifies that the H2 database is file-based
                .option(ConnectionFactoryOptions.DATABASE, "./testDb") // Specifies the database file path for testing
                .option(ConnectionFactoryOptions.USER, "kuku") // Database username
                .option(ConnectionFactoryOptions.PASSWORD, "kuku") // Database password
                .build();
        return ConnectionFactories.get(options);
    }

    /**
     * Initializes the database by executing the SQL script for schema creation.
     * This method reads the SQL script from the classpath and runs it against the database.
     *
     * We do not do anything here for now. It exists just for our knowledge
     */
    @Bean
    public ConnectionFactoryInitializer initializeDatabase() {
        var cfi = new ConnectionFactoryInitializer();
        cfi.setConnectionFactory(testConnectionFactory());
        cfi.setEnabled(true);
        return cfi;
    }


}
