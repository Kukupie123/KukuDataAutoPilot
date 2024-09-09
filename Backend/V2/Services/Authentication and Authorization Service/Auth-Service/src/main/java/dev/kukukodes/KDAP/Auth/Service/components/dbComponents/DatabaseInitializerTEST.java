package dev.kukukodes.KDAP.Auth.Service.components.dbComponents;

import dev.kukukodes.KDAP.Auth.Service.entity.UserDbLevel;
import dev.kukukodes.KDAP.Auth.Service.enums.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;

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
    private R2dbcEntityTemplate template;

    /**
     * Creates and inserts a root user into the database when the application context is fully initialized.
     *
     * <p>This method is triggered by the {@link ContextRefreshedEvent} event, which indicates that the application
     * context is refreshed and fully initialized. It performs the following steps:
     * - Initializes a {@link UserDbLevel} object with properties such as user ID, password, description, status, and
     * timestamps for creation and last activity.
     * - Uses the {@link R2dbcEntityTemplate} to insert the newly created root user into the database.
     * - Logs the creation of the root user with its ID and password for confirmation.
     *
     * @param event The event object containing details about the context refresh.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
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

        template.insert(rootUser).block();
        log.info("Created Root user {} : {}", userID, password);
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
