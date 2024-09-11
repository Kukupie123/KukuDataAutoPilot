package dev.kukukodes.KDAP.Auth.Service.db.components;

import dev.kukukodes.KDAP.Auth.Service.db.entity.UserDbLevel;
import dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.extra.TableSchemaDefinition;
import dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.TableQueryGenerator;
import dev.kukukodes.KDAP.Auth.Service.db.extras.TableQueryGenerator.implementation.postgres.PostgresDialectAdapter;
import dev.kukukodes.KDAP.Auth.Service.db.repo.IUserRepository;
import dev.kukukodes.KDAP.Auth.Service.user.enums.UserStatus;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    final Dotenv dotenv = Dotenv.configure().directory("./").load();

    @Autowired
    PasswordEncoder passwordEncoder;

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
        //Create Tables first
        var postgresTableQueryAdapter = new PostgresDialectAdapter();
        String query = TableQueryGenerator.createUserTable(postgresTableQueryAdapter, TableSchemaDefinition.getUserTableColumns());
        template.getDatabaseClient().sql(query).then().block();
        template.insert(createRootAuthUser()).block();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

    ///Create user with full control
    UserDbLevel createRootAuthUser() {
        String id = dotenv.get("ROOT_USER_ID_TEST");
        String pwd = dotenv.get("ROOT_USER_PASS_TEST");
        log.info("Creating Root User < {} > for test profile", id);
        UserDbLevel rootUser = new UserDbLevel();
        rootUser.setCreated(LocalDate.now());
        rootUser.setUpdated(LocalDate.now());
        rootUser.setUserDesc("Default Root User");
        rootUser.setStatus(UserStatus.ACTIVE.toString());
        rootUser.setLastActivity(LocalDate.now());
        rootUser.setPasswordHash(passwordEncoder.encode(pwd));
        rootUser.setUserID(id);
        return rootUser;
    }
}
