package dev.kukukodes.kdap.authenticationservice.wrapper;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtClaimsAndSubjectWrapper {
    private final Claims claims;
    private final String subject;

}
