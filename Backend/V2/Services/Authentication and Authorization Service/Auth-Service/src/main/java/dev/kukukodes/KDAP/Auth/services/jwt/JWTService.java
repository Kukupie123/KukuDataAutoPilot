package dev.kukukodes.KDAP.Auth.services.jwt;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JWTService {
    private final Dotenv dotenv;

    public JWTService() {
        // Load the environment variables from the .env file
        this.dotenv = Dotenv.configure().directory("./").load();
    }

    public String generateJWTToken(Map<String, Object> claims, String subject) {
        Claims builtClaim = Jwts.claims()
                .subject(subject)
                .add(claims)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .build();

        return Jwts.builder()
                .setClaims(builtClaim) // Fix: setClaims() instead of claims().add()
                .signWith(generateKey())
                .compact();
    }

    public Claims extractClaims(String jwtToken) throws JwtException {
        return Jwts.parser()
                .verifyWith(generateKey()) // Use the same key to validate
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }


    private SecretKey generateKey() {
        // Get the secret key from the .env file
        String secretKey = dotenv.get("JWT_KEY");
        // Convert the secret key to a Key object
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
