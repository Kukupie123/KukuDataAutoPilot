package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import dev.kukukodes.kdap.authenticationservice.service.CacheService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class ServiceBeans {

    @Bean()
    @Primary
    @Qualifier(AccessLevelConst.SELF)
    public UserService userService(IUserRepo userRepo, CacheService cacheService, SecurityHelper securityHelper, UserEventPublisher userEventPublisher, Environment environment) {
        return new UserService(userRepo, cacheService, securityHelper, userEventPublisher, false);
    }

    @Bean()
    @Qualifier(AccessLevelConst.ADMIN)
    public UserService userServiceAdmin(IUserRepo userRepo, CacheService cacheService, SecurityHelper securityHelper, UserEventPublisher userEventPublisher, Environment environment) {
        return new UserService(userRepo, cacheService, securityHelper, userEventPublisher, true);
    }
}
