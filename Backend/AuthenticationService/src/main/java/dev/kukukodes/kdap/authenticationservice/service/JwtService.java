package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final String key;

    public JwtService(@Value("${jwtkey}") String jwtKey) {
        this.key = jwtKey;
    }
    public String generateJwtToken(Claims claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(claims.getSubject())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofDays(5))))
                .signWith(generateKey())
                .compact()
                ;
    }

    public String generateJwtToken(Map<String, String> claims, String subject) {
        var claimsObj = Jwts.claims().add(claims).subject(subject).build();
        return generateJwtToken(claimsObj);
    }
    public Claims extractClaimsFromJwtToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                ;
    }

    /**
     * Create claim with id as subject
     */
    public Claims createClaimsForUser(KDAPUserEntity user) {
        return Jwts.claims().subject(user.getId()).build();
    }

    private SecretKey generateKey() {
        return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
