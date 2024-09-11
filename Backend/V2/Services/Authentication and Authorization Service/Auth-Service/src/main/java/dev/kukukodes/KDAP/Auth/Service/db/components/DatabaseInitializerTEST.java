package dev.kukukodes.KDAP.Auth.Service.db.components;

import dev.kukukodes.KDAP.Auth.Service.db.entity.UserDbLevel;
import dev.kukukodes.KDAP.Auth.Service.db.repo.IUserRepository;
import dev.kukukodes.KDAP.Auth.Service.user.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Initializes the test database with a root user after the Spring application context is fully refreshed.
 *
 * <p>This component is used to set up necessary data for testing purposes by creating a default root user with
 * credentials specified in the application properties. It listens for the {@link ContextRefreshedEvent} event,
 * which signals that the application context has been initialized and is ready for further configuration.
 *
 * <p>The {@link #onApplicationEvent(ContextRefreshedEvent)} method:
 * - Logs the initialization process of the root user.
 * - Creates a new instance of {@link UserDbLevel} and sets its properties using values retrieved from the application
 * properties (such as user ID and password) and the current date for creation and activity timestamps.
 * - Inserts the root user into the database using the {@link R2dbcEntityTemplate}.
 * - Logs a message indicating that the root user has been successfully created.
 *
 * <p>Note: This class is annotated with {@link Profile} to ensure that it is only active in the "test" profile.
 * It implements {@link ApplicationListener} to react to application context events and perform database initialization
 * as needed.
 */
@Slf4j
@Component
@Profile("test")
public class DatabaseInitializerTEST implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${DB.ROOT.ID}")
    String userID;

    @Value("${DB.ROOT.PASS}")
    String password;

    @Autowired
    IUserRepository userRepository;
    @Autowired
    private R2dbcEntityTemplate template;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        //Sometimes tables are not created even if ensure so doing it after here
        Mono.fromRunnable(() -> {
            try {
                log.info("Creating Test Tables");
                Resource resource = new ClassPathResource("db/test/create_table.sql");
                String sqlCommand = resource.getContentAsString(StandardCharsets.UTF_8);
                template.getDatabaseClient().sql(sqlCommand).then().block();
                log.info("Creating Root User as described in test.property");
                UserDbLevel rootUser = new UserDbLevel();
                rootUser.setUserID(userID);
                rootUser.setPasswordHash(password);
                rootUser.setUserDesc("Default Root User");
                rootUser.setStatus(UserStatus.ACTIVE.toString());
                rootUser.setCreated(LocalDate.now());
                rootUser.setUpdated(LocalDate.now());
                rootUser.setLastActivity(LocalDate.now());

                //TODO: Complete the stuff mentioned below
                log.info("Creating Root Permission: ROOT");
                log.info("Creating Root Role: ROOT");
                log.info("Creating Fundamental operations:");

                userRepository.addUser(rootUser).block();
                log.info("Created Root user {} : {}", userID, password);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }).block();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
