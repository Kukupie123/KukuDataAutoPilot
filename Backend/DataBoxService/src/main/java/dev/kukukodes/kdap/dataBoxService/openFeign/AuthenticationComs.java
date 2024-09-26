package dev.kukukodes.kdap.dataBoxService.openFeign;

import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(value = "${auth.name}", url = "http://localhost:${auth.port}/api/authenticated")
public interface AuthenticationComs {
    /**
     * Get user info from client's bearer token
     *
     * @param clientToken bearer token
     */
    @GetMapping("/")
    ResponseEntity<ResponseModel<List<KDAPUser>>> getUserInfoFromClientToken(@RequestHeader("Authorization") String clientToken);

    /**
     * Get information about the user
     *
     * @param internalJWTToken internal generated jwt token which contains source claim
     */
    @GetMapping("/{userID}")
    ResponseEntity<ResponseModel<List<KDAPUser>>> getUserInfo(@PathVariable String userID, @RequestHeader("Authorization") String internalJWTToken);

}
