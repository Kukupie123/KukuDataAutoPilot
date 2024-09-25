package dev.kukukodes.kdap.dataBoxService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DataStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataStoreApplication.class, args);
    }

    //TODO: Role check for data entry
    //TODO: Limit option for gets
    //TODO: Inter service authentication
}
