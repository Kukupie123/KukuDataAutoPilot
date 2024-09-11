package dev.kukukodes.KDAP.Auth.Service.jwt;

import dev.kukukodes.KDAP.Auth.Service.jwt.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class JWTTest {
    @Autowired
    private JWTService jwtService;

    @Test
    void jwtTokenValidationTest(){
        String token = jwtService.generateJWTToken(new HashMap<>(),"dummy");
        System.out.println(token);

    }
}
