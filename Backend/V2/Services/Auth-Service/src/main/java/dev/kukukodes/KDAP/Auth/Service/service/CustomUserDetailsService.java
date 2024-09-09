package dev.kukukodes.KDAP.Auth.Service.service;


import dev.kukukodes.KDAP.Auth.Service.entity.CustomUserDetails;
import dev.kukukodes.KDAP.Auth.Service.repo.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private IUserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("Finding User {}", username);
        return userRepository.getUsersByUserId(username)
                .next()
                .map(
                        userDbLevel -> (UserDetails) new CustomUserDetails(userDbLevel.getId(), userDbLevel.getUserID(), userDbLevel.getPasswordHash())
                )
                .switchIfEmpty(Mono.empty());
    }
}
