package dev.kukukodes.KDAP.Auth.Service.components.authComponents;


import dev.kukukodes.KDAP.Auth.Service.entity.CustomUserDetails;
import dev.kukukodes.KDAP.Auth.Service.repo.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom implementation of the {@link ReactiveUserDetailsService} interface, responsible for loading user details
 * from a data source in a reactive manner within a Spring Security context.
 *
 * <p>This component interacts with the {@link IUserRepository} to retrieve user data and transform it into a
 * {@link UserDetails} object. It provides a reactive approach to fetching user details for authentication purposes.
 *
 * <p>The {@link #findByUsername(String)} method:
 * - Takes a username as input and queries the user repository to fetch user data associated with that username.
 * - Maps the retrieved data into a {@link CustomUserDetails} object, which is then returned as a {@link UserDetails} instance.
 * - If the user is not found in the repository, it logs this information and returns an empty {@link Mono}.
 *
 * <p>This class is used by Spring Security to retrieve user details during the authentication process, providing
 * a reactive and asynchronous approach to user data retrieval.
 */
@Slf4j
@Component
public class CustomUserDetailsService implements ReactiveUserDetailsService {


    @Autowired
    private IUserRepository userRepository;

    /**
     * Retrieves user details by username from the user repository.
     *
     * <p>This method queries the {@link IUserRepository} to find a user with the given username and maps the result
     * to a {@link CustomUserDetails} object. If no user is found, it logs the event and returns an empty {@link Mono}.
     *
     * @param username The username of the user whose details are to be retrieved.
     * @return A {@link Mono} containing the {@link UserDetails} for the specified username, or an empty {@link Mono}
     * if the user is not found.
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("Finding User {}", username);
        return userRepository.getUsersByUserId(username)
                .map(
                        userDbLevel -> {
                            CustomUserDetails user = new CustomUserDetails(userDbLevel.getId(), userDbLevel.getUserID(), userDbLevel.getPasswordHash());
                            log.info("Found user {}", user);
                            return (UserDetails) user;
                        }
                )
                .switchIfEmpty(Mono.defer(() -> {
                            log.info("User {} not found", username);
                            return Mono.empty();
                        })
                ).next();
    }
}
