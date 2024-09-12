package dev.kukukodes.KDAP.Auth.components.database;

import dev.kukukodes.KDAP.Auth.constants.auth.AuthConstants;
import dev.kukukodes.KDAP.Auth.entities.database.RoleEntity;
import dev.kukukodes.KDAP.Auth.entities.database.UserEntity;
import dev.kukukodes.KDAP.Auth.database.tableQueryDialect.TableQueryDialectGenerator;
import dev.kukukodes.KDAP.Auth.enums.user.UserStatus;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

/**
 * Initializes the test database with a root user after the Spring application context is fully refreshed.
 *
 * <p>This component is used to set up necessary data for testing purposes by creating a default root user with
 * credentials specified in the application properties. It listens for the {@link ContextRefreshedEvent} event,
 * which signals that the application context has been initialized and is ready for further configuration.
 *
 * <p>The {@link #onApplicationEvent(ContextRefreshedEvent)} method:
 * - Logs the initialization process of the root user.
 * - Creates a new instance of {@link UserEntity} and sets its properties using values retrieved from the application
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
public class DbInitTestProfile implements ApplicationListener<ContextRefreshedEvent> {
    final Dotenv dotenv = Dotenv.configure().directory("./").load();

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TableQueryDialectGenerator tableQueryDialectGenerator;
    @Autowired
    private R2dbcEntityTemplate template;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Dropping existing tables");
        //Drop existing table
        String query = tableQueryDialectGenerator.userGenerator.dropUserTable(); //User
        template.getDatabaseClient().sql(query).then().block();
        query = tableQueryDialectGenerator.roleGenerator.dropRoleTable(); //Role
        template.getDatabaseClient().sql(query).then().block();

        //Create Tables first
        log.info("Creating new tables");
        query = tableQueryDialectGenerator.userGenerator.createUserTable(); //User
        template.getDatabaseClient().sql(query).then().block();
        query = tableQueryDialectGenerator.roleGenerator.createRoleTable(); //Role
        template.getDatabaseClient().sql(query).then().block();
        //Create role
        var rootRole = createRootRole();
        log.info("Creating root role {}", rootRole);
        template.insert(rootRole).then().block();
        //Create user
        var rootUser = createRootAuthUser();
        log.info("adding root user {}", rootUser.toString());
        template.insert(rootUser).block();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

    ///Create user with full control
    UserEntity createRootAuthUser() {
        String id = dotenv.get(AuthConstants.RootUser.RootUserName);
        String pwd = dotenv.get(AuthConstants.RootUser.RootUserPassword);
        var currentDate = new Date();
        log.info("Creating Root User < {} > for test profile", id);
        UserEntity rootUser = new UserEntity();
        rootUser.setCreated(currentDate);
        rootUser.setUpdated(currentDate);
        rootUser.setUserDesc("Default Root User");
        rootUser.setStatus(UserStatus.ACTIVE.toString());
        rootUser.setLastActivity(currentDate);
        rootUser.setPasswordHash(passwordEncoder.encode(pwd));
        rootUser.setName(id);
        return rootUser;
    }

    ///Create root role
    RoleEntity createRootRole() {
        var role = new RoleEntity();
        var currentDate = new Date();
        role.setName(AuthConstants.RootUser.RootRoleName);
        role.setCreated(currentDate);
        role.setUpdated(currentDate);
        role.setDesc("Root User ROLE with global access");
        return role;
    }
}
