package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import io.jsonwebtoken.Jwts;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateUserJwtToken(UserEntity user) {
        Map<String,String> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("pic", user.getPicture());
        Jwts.builder()
        .claims(claims)
        .subject(user.getId())
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(5))))
        .signWith(null)
        ;
        return "THIS IS DUMMY JWT for " + user.toString();
    }
}
