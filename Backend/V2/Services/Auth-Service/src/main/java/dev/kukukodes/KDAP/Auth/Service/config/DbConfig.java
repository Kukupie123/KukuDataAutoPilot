package dev.kukukodes.KDAP.Auth.Service.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

/**
 * Configuration class for setting up R2DBC database connections and initializing the database schema.
 *
 * <p>1. Defines the ConnectionFactory beans for different profiles (test and dev) to configure database connections.
 * 2. Defines the ConnectionFactoryInitializer bean for the test profile to initialize the database schema using an SQL script.
 */
@Configuration
public class DbConfig {
    private static final Logger log = LoggerFactory.getLogger(DbConfig.class);

    /**
     * Creates a ConnectionFactory bean for the 'test' profile using an H2 database.
     *
     * @return ConnectionFactory configured for the test profile.
     */
    @Bean
    @Profile("test")
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
     * Creates a ConnectionFactoryInitializer bean for the 'test' profile to initialize the database schema.
     *
     * <p>This bean sets up the ConnectionFactoryInitializer to execute the SQL schema script located in the 'db/test/create_table.sql'
     * file to set up the necessary tables and indices for the test database.
     *
     * @param testCF The ConnectionFactory to be used for schema initialization.
     * @return ConnectionFactoryInitializer configured to run the schema script.
     */
    @Bean
    @Profile("test")
    public ConnectionFactoryInitializer testConnectionFactoryInitializer(ConnectionFactory testCF) {
        log.info("Initializing TEST Connection Factory with schema");
        var cfi = new ConnectionFactoryInitializer();
        cfi.setConnectionFactory(testCF);
        cfi.setDatabasePopulator(
                new ResourceDatabasePopulator(
                        new ClassPathResource("db/test/create_table.sql") // Path to the SQL script for initializing the test database schema
                )
        );
        return cfi;
    }
}
