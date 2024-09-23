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

}
