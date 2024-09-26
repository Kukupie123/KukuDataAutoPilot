package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.constants.RequestSourceConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Communication between different services
 */
@Component
public class ServiceCommunicationHelper {
    @Value("${jwtKey}")
    private String jwtKey;

    /**
     * Generate token that needs to be passed as Authorization Bearer token
     *
     * @return internal request token
     */
    public String generateToken() {
        Claims claims = Jwts.claims().add("source", RequestSourceConst.INTERNAL).build();
        return Jwts.builder().claims(claims).signWith(generateKey()).compact();
    }

    private SecretKey generateKey() {
        return new SecretKeySpec(jwtKey.getBytes(), "HmacSHA256");
    }
}
