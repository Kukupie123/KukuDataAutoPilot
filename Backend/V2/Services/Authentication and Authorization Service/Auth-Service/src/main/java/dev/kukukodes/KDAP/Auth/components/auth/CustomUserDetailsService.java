package dev.kukukodes.KDAP.Auth.components.auth;


import dev.kukukodes.KDAP.Auth.v2.models.CustomUserDetails;
import dev.kukukodes.KDAP.Auth.repo.database.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("Finding User {}", username);
        return userRepository.getUserByUserId(username)
                .map(
                        userDbLevel -> {
                            CustomUserDetails user = new CustomUserDetails(userDbLevel.getId(), userDbLevel.getUserID(), userDbLevel.getPasswordHash());
                            log.info("Found user {}", user);
                            return (UserDetails) user;
                        }
                )
                .switchIfEmpty(Mono.empty());
    }
}
