package dev.kukukodes.KDAP.Auth.Service.auth.components.UserDetailsService;


import dev.kukukodes.KDAP.Auth.Service.auth.entity.CustomUserDetails;
import dev.kukukodes.KDAP.Auth.Service.user.repo.IUserRepository;
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
