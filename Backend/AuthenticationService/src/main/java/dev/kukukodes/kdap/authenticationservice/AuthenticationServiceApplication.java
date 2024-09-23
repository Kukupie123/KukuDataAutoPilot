package dev.kukukodes.kdap.authenticationservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@SpringBootApplication
@EnableCaching
@EnableRabbit
public class AuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }

    //TODO: Create super user and only allow super operations for super user such as deleting, getting all users
    //TODO: Store user authentication once authenticated (if not done yet). Use this for user specific operation in user service. currently everyone has super access so its bad.

}
