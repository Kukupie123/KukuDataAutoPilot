package dev.kukukodes.kdap.dataBoxService.interServiceCommunication.clients;

import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "authentication", url = "http://localhost:${auth.port}/api")
public interface AuthenticationServiceComs {
    /**
     * Get user info
     *
     * @param bearerToken jwt token
     * @param userID      userID to get info of
     * @return {@link KDAPUser}
     */
    @GetMapping("/authenticated/")
    ResponseEntity<ResponseModel<KDAPUser>> getUserData(@RequestHeader("Authorization") String bearerToken);
}
