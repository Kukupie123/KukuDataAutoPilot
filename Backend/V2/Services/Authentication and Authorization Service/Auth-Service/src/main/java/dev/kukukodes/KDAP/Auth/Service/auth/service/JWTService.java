package dev.kukukodes.KDAP.Auth.Service.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {
    public String generateToken(String subject) {
        Map<String, Object> claims = new HashMap<>();
        try {
            var sk = KeyGenerator.getInstance("HmacSHA256").generateKey();
            Key key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(sk.getEncoded()));
            return Jwts.builder().claims()
                    .add(claims)
                    .subject(subject)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 60))
                    .and()
                    .signWith(key)
                    .compact();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
