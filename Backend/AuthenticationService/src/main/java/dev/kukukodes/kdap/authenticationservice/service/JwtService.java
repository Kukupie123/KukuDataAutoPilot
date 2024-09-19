package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.constants.EnvNamesConst;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.wrapper.JwtClaimsAndSubjectWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Service
public class JwtService {

    private final String key = System.getenv(EnvNamesConst.JWT_KEY);

    public String generateJwtToken(JwtClaimsAndSubjectWrapper jwtClaimsAndSubject) {
        return Jwts.builder()
                .claims(jwtClaimsAndSubject.getClaims())
                .subject(jwtClaimsAndSubject.getSubject())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(5))))
                .signWith(generateKey())
                .compact()
                ;
    }

    public String generateJwtToken(Map<String, String> claims, String subject) {
        var claimsObj = Jwts.claims().add(claims).build();
        return generateJwtToken(new JwtClaimsAndSubjectWrapper(claimsObj, subject));
    }

    public Claims extractClaimsFromJwtToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                ;
    }

    public SecretKey generateKey() {
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
