package dev.kukukodes.KDAP.Auth.components.database;

import dev.kukukodes.KDAP.Auth.constants.auth.AuthConstants;
import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.entities.database.*;
import dev.kukukodes.KDAP.Auth.enums.user.UserStatus;
import dev.kukukodes.KDAP.Auth.repo.database.*;
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
    IUserRepository userRoleRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    IPermissionRepository permissionRepository;
    @Autowired
    IOperationRepository operationRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Creating root operation, permission, role and user");
        //Creating root user
        String name = dotenv.get(AuthConstants.RootUser.ENV_RootUserName);
        var rootUser = new UserEntity(name, passwordEncoder.encode(dotenv.get(AuthConstants.RootUser.ENV_RootUserPassword)), "Root user", new Date(), new Date(), new Date(), UserStatus.ACTIVE.toString());
        userRoleRepository.addUser(rootUser).then().block();
        //Creating root role
        var rootRole = new RoleEntity(dotenv.get(AuthConstants.RootUser.ENV_RootRoleName), "Root role", new Date(), new Date());
        roleRepository.addRole(rootRole).then().block();
        //Creating root permission
        var rootPermission = new PermissionEntity("*", "Root permission", new Date(), new Date());
        permissionRepository.addPermission(rootPermission).block();
        //Creating root operation
        var rootOp = new OperationEntity("*", "Root operation", new Date(), new Date());
        operationRepository.addOperation(rootOp).block();
        log.info("Populating junction tables");
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

}
