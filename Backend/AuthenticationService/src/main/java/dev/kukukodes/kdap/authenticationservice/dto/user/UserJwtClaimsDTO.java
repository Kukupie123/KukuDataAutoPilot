package dev.kukukodes.kdap.authenticationservice.dto.user;

import io.jsonwebtoken.Claims;
import lombok.Getter;

/**
 * DTO that uses claims to construct a 'user'
 */
@Getter
public class UserJwtClaimsDTO {
    private final String id;
    private final String email;
    private final String name;
    private final String pic;

    public UserJwtClaimsDTO(Claims claims) {
        this.id = claims.getSubject();
        this.email = claims.get("email",String.class);
        this.name = claims.get("name",String.class);
        this.pic= claims.get("pic",String.class);
    }


}
