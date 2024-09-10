package dev.kukukodes.KDAP.Auth.Service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class IdPasswordAuthenticationDTO {
    public String id;
    public String password;
}
