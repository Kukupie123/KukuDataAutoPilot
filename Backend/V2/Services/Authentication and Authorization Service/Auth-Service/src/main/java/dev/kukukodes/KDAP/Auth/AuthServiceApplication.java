package dev.kukukodes.KDAP.Auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	//TODO: Dependency injection scope fix for many classes and beans such as repositories, template, etc

}
