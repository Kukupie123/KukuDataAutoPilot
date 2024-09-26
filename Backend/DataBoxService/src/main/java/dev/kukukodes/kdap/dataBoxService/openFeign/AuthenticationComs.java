package dev.kukukodes.kdap.dataBoxService.openFeign;

import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Feign client for interacting with the authentication service.
 */
@FeignClient(value = "${auth.name}", url = "http://localhost:${auth.port}/api/authenticated")
public interface AuthenticationComs {

    /**
     * Retrieve user information based on the provided client's bearer token.
     *
     * @param clientToken the bearer token of the client.
     * @return ResponseEntity containing the ResponseModel with a list of KDAPUser.
     */
    @GetMapping("/")
    ResponseEntity<ResponseModel<List<KDAPUser>>> getUserInfoFromClientToken(@RequestHeader("Authorization") String clientToken);

    /**
     * Fetch detailed information about a specific user using their user ID.
     *
     * @param userID            the unique identifier of the user to retrieve.
     * @param internalJWTToken  the internal JWT token that contains the source claim.
     * @return ResponseEntity containing the ResponseModel with a list of KDAPUser.
     */
    @GetMapping("/{userID}")
    ResponseEntity<ResponseModel<List<KDAPUser>>> getUserInfobyID(@PathVariable String userID, @RequestHeader("Authorization") String internalJWTToken);
}
