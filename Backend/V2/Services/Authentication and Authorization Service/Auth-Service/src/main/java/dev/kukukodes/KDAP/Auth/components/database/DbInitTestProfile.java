package dev.kukukodes.KDAP.Auth.components.database;

import dev.kukukodes.KDAP.Auth.constants.auth.AuthConstants;
import dev.kukukodes.KDAP.Auth.entities.database.OperationEntity;
import dev.kukukodes.KDAP.Auth.entities.database.PermissionEntity;
import dev.kukukodes.KDAP.Auth.entities.database.RoleEntity;
import dev.kukukodes.KDAP.Auth.entities.database.UserEntity;
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
    private R2dbcEntityTemplate template;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Creating root operation, permission, role and user");
        //Create operation
        var operation = createRootOperation();
        template.insert(operation).then().block();
        //Create permission
        var permission = createRootPermission();
        template.insert(permission).then().block();
        //Create role
        var rootRole = createRootRole();
        template.insert(rootRole).then().block();
        //Create user
        var rootUser = createRootAuthUser();
        template.insert(rootUser).block();
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

    ///Create user with full control
    UserEntity createRootAuthUser() {
        String id = dotenv.get(AuthConstants.RootUser.ENV_RootUserName);
        String pwd = dotenv.get(AuthConstants.RootUser.ENV_RootUserPassword);
        var currentDate = new Date();
        log.info("Creating Root User < {} > for test profile", id);
        return new UserEntity(id, passwordEncoder.encode(pwd), "Default Root user", currentDate, currentDate, currentDate, UserStatus.ACTIVE.toString());
    }

    ///Create root role
    RoleEntity createRootRole() {
        var currentDate = new Date();
        return new RoleEntity(AuthConstants.RootUser.ENV_RootRoleName, "ROOT User Role", currentDate, currentDate);
    }

    //create root permission
    PermissionEntity createRootPermission() {
        var date = new Date();
        return new PermissionEntity(dotenv.get(AuthConstants.RootUser.ENV_RootUserPermission), "Root permission", date, date);
    }

    //create root operation
    OperationEntity createRootOperation() {
        var date = new Date();
        return new OperationEntity(dotenv.get(AuthConstants.RootUser.ENV_RootUserOperation), "Root operation", date, date);
    }
}
