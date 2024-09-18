package dev.kukukodes.kdap.authenticationservice.service.userService.impl;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import dev.kukukodes.kdap.authenticationservice.service.userService.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService implements IUserService {
    private final IUserRepo userRepo;

    UserService(@Autowired IUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Mono<UserEntity> addUser(UserEntity user) {
        return userRepo.addUser(user);
    }

    @Override
    public Mono<UserEntity> updateUser(UserEntity user) {
        return userRepo.updateUser(user);
    }

    @Override
    public Mono<UserEntity> AddUpdateUser(UserEntity user) {
        return getUserById(user.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No existing user found. Adding new one");
                    return userRepo.addUser(user);
                }))
                .flatMap(userEntity -> {
                    log.info("Existing user found. Updating user");
                    return userRepo.updateUser(userEntity);
                })
                ;
    }

    @Override
    public Mono<UserEntity> getUserById(String id) {
        return userRepo.getUserByID(id);
    }
}
