package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceUnitTest {

    private static final String TEST_JWT_KEY = "testJwtKeyWithAtLeast32Characters!!!";
    private final JwtService jwtService = new JwtService(TEST_JWT_KEY);

    @Test
    void generateJwtToken_withClaims_shouldReturnValidToken() {
        // Arrange
        Claims claims = Jwts.claims().subject("testSubject").build();

        // Act
        String token = jwtService.generateJwtToken(claims);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // Check if it's a valid JWT format
    }

    @Test
    void generateJwtToken_withMapAndSubject_shouldReturnValidToken() {
        // Arrange
        Map<String, String> claims = new HashMap<>();
        claims.put("key", "value");
        String subject = "testSubject";

        // Act
        String token = jwtService.generateJwtToken(claims, subject);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // Check if it's a valid JWT format
    }

    @Test
    void extractClaimsFromJwtToken_withValidToken_shouldReturnClaims() {
        // Arrange
        Claims originalClaims = Jwts.claims().subject("testSubject").build();
        String token = jwtService.generateJwtToken(originalClaims);

        // Act
        Claims extractedClaims = jwtService.extractClaimsFromJwtToken(token);

        // Assert
        assertNotNull(extractedClaims);
        assertEquals("testSubject", extractedClaims.getSubject());
    }

    @Test
    void createClaimsForUser_shouldReturnClaimsWithUserIdAsSubject() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setId("testUserId");

        // Act
        Claims claims = jwtService.createClaimsForUser(user);

        // Assert
        assertNotNull(claims);
        assertEquals("testUserId", claims.getSubject());
    }

    @Test
    void extractClaimsFromJwtToken_withInvalidToken_shouldThrowException() {
        // Arrange
        String invalidToken = "invalidToken";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtService.extractClaimsFromJwtToken(invalidToken);
        });
    }
}
