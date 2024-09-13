package dev.kukukodes.KDAP.Auth.configurations.database;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
@Slf4j
@Profile("test")
public class DbConfigTestProfile {

    @Bean
    public ConnectionFactory connectionFactory() {
        var postgresConfig = PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432)
                .password("root")
                .username("postgres")
                .database("kdap")
                .schema("test")
                .build();
        return new PostgresqlConnectionFactory(postgresConfig);
    }

    @Bean
        ///Drop existing tables, create a new one
    ConnectionFactoryInitializer connectionFactoryInitializer() {
        var cfi = new ConnectionFactoryInitializer();
        cfi.setConnectionFactory(connectionFactory());
        cfi.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("database/queries/dropTables/postgres.sql"),
                new ClassPathResource("database/queries/create_tables/postgres.sql")
        ));
        return cfi;
    }

}
