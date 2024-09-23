package dev.kukukodes.kdap.dataBoxService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Properties;

@SpringBootApplication
@EnableFeignClients
public class DataStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataStoreApplication.class, args);
    }

    //TODO: Verification for userID for data box and data entry
    //TODO: caching for services
    //TODO: Service tests once complete
    //TODO: message broker
    //TODO: Super role check again.
}
