package dev.kukukodes.kdap.dataBoxService.interServiceCommunication.clients;

import dev.kukukodes.kdap.dataBoxService.dto.KDAPUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "authentication", url = "http://localhost:${auth.port}/api")
public interface AuthenticationServiceComs {

    @GetMapping("/authenticated/")
    KDAPUserDTO getUserData(@RequestHeader("Authorization") String bearerToken);
}
