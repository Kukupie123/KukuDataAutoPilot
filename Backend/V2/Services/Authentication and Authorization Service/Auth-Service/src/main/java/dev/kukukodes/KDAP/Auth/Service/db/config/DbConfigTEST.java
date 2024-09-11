package dev.kukukodes.KDAP.Auth.Service.db.config;

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
public class DbConfigTEST {

    @Bean
    public ConnectionFactory connectionFactory() {
        var postgresConfig = PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(54321)
                .password("root")
                .username("postgres")
                .database("kdap")
                .schema("test")
                .build();
        return new PostgresqlConnectionFactory(postgresConfig);
    }

}
