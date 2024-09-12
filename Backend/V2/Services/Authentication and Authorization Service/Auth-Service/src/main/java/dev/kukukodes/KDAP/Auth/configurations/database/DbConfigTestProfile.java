package dev.kukukodes.KDAP.Auth.configurations.database;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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

}
