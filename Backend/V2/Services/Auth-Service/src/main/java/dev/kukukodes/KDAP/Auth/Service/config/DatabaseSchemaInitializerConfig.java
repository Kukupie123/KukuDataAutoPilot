package dev.kukukodes.KDAP.Auth.Service.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Initializes the database with the tables
 */
@Component
public class DatabaseSchemaInitializerConfig {

    private final DatabaseClient databaseClient;
    private final Logger logger = LoggerFactory.getLogger(DatabaseSchemaInitializerConfig.class);

    public DatabaseSchemaInitializerConfig(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }


    @PostConstruct
    public void initializeSchema() {
        try {
            logger.debug("Creating test table");
            // Load the schema file from resources
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("h2_schema.sql");
            if (schemaStream == null) {
                throw new RuntimeException("Schema file not found");
            }

            // Read the schema file into a String
            String schema = StreamUtils.copyToString(schemaStream, StandardCharsets.UTF_8);
            logger.debug(schema);

            // Execute the SQL schema on the H2 database
            databaseClient.sql(schema)
                    .then()
                    .subscribe();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }
}
