package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.constants.RequestSourceConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Date;

/**
 * Communication between different services
 */
@Slf4j
@Component
public class ServiceCommunicationHelper {
    private final String jwtKey;

    public ServiceCommunicationHelper(@Value("${jwtkey}") String jwtKey) {
        this.jwtKey = jwtKey;
        log.info("JWT KEY IS {}", jwtKey);
    }

    /**
     * Generate token that needs to be passed as Authorization Bearer token
     *
     * @return internal request token
     */
    public String generateToken() {
        Claims claims = Jwts.claims().add("source", RequestSourceConst.INTERNAL).build();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey() {
        return new SecretKeySpec(jwtKey.getBytes(), "HmacSHA256");
    }
}
