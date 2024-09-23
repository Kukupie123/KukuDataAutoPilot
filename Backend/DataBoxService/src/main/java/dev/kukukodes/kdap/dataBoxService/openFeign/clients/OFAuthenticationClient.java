package dev.kukukodes.kdap.dataBoxService.openFeign.clients;

import dev.kukukodes.kdap.dataBoxService.dto.UserDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "authentication", url = "http://localhost:8080/api")
public interface OFAuthenticationClient {

    @GetMapping("/authenticated/")
    ResponseEntity<String> getUserData(@RequestHeader("Authorization") String bearerToken);
}
